package ru.yandex.practicum.filmorate.storage.user;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.jdbc.support.rowset.SqlRowSet;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.filmorate.exception.NoSuchItemException;
import ru.yandex.practicum.filmorate.model.User;

import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.List;
import java.util.Objects;

@Repository
public class UserDbStorage implements UserStorage {

    private final JdbcTemplate jdbcTemplate;

    @Autowired
    public UserDbStorage(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    @Override
    public User addNew(User user) {
        String sqlQuery = "INSERT INTO USERS (LOGIN, USER_NAME, EMAIL, BIRTHDAY) VALUES (?, ?, ?, ?);";
        KeyHolder keyHolder = new GeneratedKeyHolder();
        jdbcTemplate.update(connection -> {
            PreparedStatement statement = connection.prepareStatement(sqlQuery, new String[]{"USER_ID"});
            statement.setString(1, user.getLogin());
            statement.setString(2, user.getName());
            statement.setString(3, user.getEmail());
            statement.setDate(4, Date.valueOf(user.getBirthday()));
            return statement;
        }, keyHolder);
        user.setId(Objects.requireNonNull(keyHolder.getKey()).intValue());
        return user;
    }

    @Override
    public User update(User user) {
        String sqlQuery = "UPDATE USERS SET LOGIN = ?, USER_NAME = ?, EMAIL = ?, BIRTHDAY = ? WHERE USER_ID = ?;";
        jdbcTemplate.update(
                sqlQuery, user.getLogin(), user.getName(), user.getEmail(), user.getBirthday(), user.getId()
        );
        return user;
    }

    @Override
    public User getById(long id) {
        SqlRowSet userSearch = jdbcTemplate.queryForRowSet("SELECT * FROM USERS WHERE USER_ID = ?;", id);
        if (userSearch.next()) {
            return new User(
                    userSearch.getLong("USER_ID"),
                    userSearch.getString("LOGIN"),
                    userSearch.getString("EMAIL"),
                    userSearch.getString("USER_NAME"),
                    LocalDate.parse(userSearch.getString("BIRTHDAY")));
        } else {
            throw new NoSuchItemException("Не найден юзер");
        }
    }

    @Override
    public void deleteById(long id) {
        String sqlQuery = "DELETE FROM USERS WHERE USER_ID = ?;";
        jdbcTemplate.update(sqlQuery, id);
    }

    @Override
    public List<User> showAll() {
        String allUsers = "SELECT * FROM USERS;";
        return jdbcTemplate.query(allUsers, (rs, rowNum) -> new User(
                rs.getLong("USER_ID"),
                rs.getString("LOGIN"),
                rs.getString("EMAIL"),
                rs.getString("USER_NAME"),
                LocalDate.parse(rs.getString("BIRTHDAY")))
        );
    }

    @Override
    public void addFriend(long id, long friendId) {
        String sqlQuery = "INSERT INTO FRIENDS (USER_ID, FRIEND_ID) VALUES (?, ?);";
        jdbcTemplate.update(sqlQuery, id, friendId);
    }

    @Override
    public void deleteFriend(long id, long friendId) {
        String sqlQuery = "DELETE FROM FRIENDS WHERE USER_ID = ? AND FRIEND_ID = ?;";
        jdbcTemplate.update(sqlQuery, id, friendId);
    }

    @Override
    public List<User> showFriends(long id) {
        String friends = "SELECT U.USER_ID, U.USER_NAME, U.LOGIN, U.EMAIL, U.BIRTHDAY " +
                "FROM FRIENDS AS F INNER JOIN USERS AS U ON F.FRIEND_ID = U.USER_ID WHERE F.USER_ID = ?";
        return jdbcTemplate.query(friends, this::assembleUser, id);
    }

    @Override
    public List<User> showCommonFriends(long id, long friendId) {
        String commonFriends =
                "SELECT U.* FROM USERS U" +
                        " JOIN FRIENDS F on U.USER_ID = F.FRIEND_ID" +
                        " JOIN FRIENDS FR on U.USER_ID = FR.FRIEND_ID WHERE F.USER_ID = ? AND FR.USER_ID = ?";
        return jdbcTemplate.query(commonFriends, this::assembleUser, id, friendId);
    }

    private User assembleUser(ResultSet rs, int rowNum) throws SQLException {
        return new User(
                rs.getLong("USER_ID"),
                rs.getString("LOGIN"),
                rs.getString("EMAIL"),
                rs.getString("USER_NAME"),
                LocalDate.parse(rs.getString("BIRTHDAY")));
    }

}
