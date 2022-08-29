package ru.yandex.practicum.filmorate.storage.film;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.Mpa;
import ru.yandex.practicum.filmorate.service.GenreService;
import ru.yandex.practicum.filmorate.service.MpaService;

import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;
import java.util.Objects;
import java.util.Set;

@Repository
public class FilmDbStorage implements FilmStorage {
    private final JdbcTemplate jdbcTemplate;
    private final GenreService genreService;
    private final MpaService mpaService;

    @Autowired
    public FilmDbStorage(JdbcTemplate jdbcTemplate, GenreService genreService, MpaService mpaService) {
        this.jdbcTemplate = jdbcTemplate;
        this.genreService = genreService;
        this.mpaService = mpaService;
    }

    @Override
    public Film addNew(Film film) {
        String sqlQuery = "INSERT INTO FILMS (FILM_NAME, DESCRIPTION, RELEASE_DATE," +
                " DURATION, MPA_ID) VALUES (?, ?, ?, ?, ?)";
        KeyHolder keyHolder = new GeneratedKeyHolder();
        jdbcTemplate.update(connection -> {
            PreparedStatement statement = connection.prepareStatement(sqlQuery, new String[]{"FILM_ID"});
            statement.setString(1, film.getName());
            statement.setString(2, film.getDescription());
            statement.setDate(3, Date.valueOf(film.getReleaseDate()));
            statement.setLong(4, film.getDuration());
            statement.setInt(5, film.getMpa().getId());
            return statement;
        }, keyHolder);
        film.setId(Objects.requireNonNull(keyHolder.getKey()).intValue());
        film.setMpa(mpaService.getMpaById(film.getMpa().getId()));
        genreService.addGenresToFilm(film);
        return film;
    }

    @Override
    public Film update(Film film) {
        String sqlQuery = "UPDATE FILMS " +
                "SET FILM_NAME = ?, DESCRIPTION = ?, RELEASE_DATE = ?, DURATION = ?, MPA_ID = ? " +
                "WHERE FILM_ID = ?;";
        jdbcTemplate.update(
                sqlQuery,
                film.getName(),
                film.getDescription(),
                film.getReleaseDate(),
                film.getDuration(),
                film.getMpa().getId(),
                film.getId()
        );
        film.setMpa(mpaService.getMpaById(film.getMpa().getId()));
        genreService.addGenresToFilm(film);
        return film;
    }

    @Override
    public void deleteById(long id) {
        String sqlQuery = "DELETE FROM FILMS WHERE FILM_ID = ?;";
        jdbcTemplate.update(sqlQuery, id);
    }

    @Override
    public Film getById(long id) {
        String sqlQuery = "SELECT FILM_ID, FILM_NAME, DESCRIPTION, RELEASE_DATE, DURATION, F.MPA_ID, M.MPA_NAME " +
                "FROM FILMS F JOIN MPA M on F.MPA_ID = M.MPA_ID WHERE FILM_ID = ?;";
        return jdbcTemplate.queryForObject(sqlQuery, this::assembleFilm, id);
    }

    @Override
    public List<Film> showAll() {
        String sqlQuery = "SELECT FILM_ID, FILM_NAME, DESCRIPTION, RELEASE_DATE, DURATION FROM FILMS;";
        return jdbcTemplate.query(sqlQuery, this::assembleFilm);
    }

    @Override
    public void addLike(long id, long userId) {
        String sqlQuery = "INSERT INTO LIKES (FILM_ID, USER_ID) VALUES (?, ?);";
        jdbcTemplate.update(sqlQuery, id, userId);
    }

    @Override
    public void deleteLike(long id, long userId) {
        String sqlQuery = "DELETE FROM LIKES WHERE FILM_ID = ? AND USER_ID = ?;";
        jdbcTemplate.update(sqlQuery, id, userId);
    }

    @Override
    public Set<Long> getFilmsUserLikes(long id) {
        String sql = "SELECT USER_ID FROM LIKES WHERE FILM_ID = ?";
        return Set.copyOf(jdbcTemplate.query(sql, (rs, rowNum) -> rs.getLong("USER_ID"), id));
    }

    @Override
    public List<Film> showTopFilms(int size) {
        String sqlQuery =
                "SELECT F.FILM_ID, FILM_NAME, DESCRIPTION, RELEASE_DATE, DURATION, M.MPA_ID, M.MPA_NAME " +
                        "FROM FILMS F JOIN MPA M ON M.MPA_ID = F.MPA_ID " +
                        "LEFT JOIN LIKES AS L ON F.FILM_ID = L.FILM_ID" +
                        " GROUP BY F.FILM_ID ORDER BY COUNT(L.USER_ID) DESC LIMIT ?";
        return jdbcTemplate.query(sqlQuery, this::assembleFilm, size);
    }

    @Override
    public boolean hasUsersLike(long id, long userId) {
        String sqlQuery = "SELECT COUNT(USER_ID) FROM LIKES WHERE FILM_ID = ? AND USER_ID = ?;";
        int like = jdbcTemplate.queryForObject(sqlQuery, Integer.class, id, userId);
        return like != 0;
    }

    private Film assembleFilm(ResultSet rs, int rowNum) throws SQLException {
        Film film = new Film(
                rs.getLong("FILM_ID"),
                rs.getString("FILM_NAME"),
                rs.getString("DESCRIPTION"),
                rs.getDate("RELEASE_DATE").toLocalDate(),
                rs.getLong("DURATION"),
                new Mpa(
                        rs.getInt("MPA_ID"),
                        rs.getString("MPA_NAME")
                )
        );
        film.setGenres(genreService.getByFilmId((int) film.getId()));
        film.setLikeList(getFilmsUserLikes(film.getId()));
        return film;
    }
}
