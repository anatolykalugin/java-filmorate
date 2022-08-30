package ru.yandex.practicum.filmorate.storage.mpa;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.filmorate.exception.NoSuchItemException;
import ru.yandex.practicum.filmorate.model.Mpa;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

@Repository
@Slf4j
public class MpaDbStorage implements MpaStorage {
    private final JdbcTemplate jdbcTemplate;

    @Autowired
    public MpaDbStorage(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    @Override
    public Mpa getById(int id) {
        try {
            String sqlQuery = "SELECT MPA_ID, MPA_NAME FROM MPA WHERE MPA_ID = ?;";
            return jdbcTemplate.queryForObject(sqlQuery, this::assembleMpa, id);
        } catch (EmptyResultDataAccessException e) {
            log.warn("Неверный айди МПА");
            throw new NoSuchItemException("Нет такого МПА");
        }
    }

    @Override
    public List<Mpa> getAllRatings() {
        String sqlQuery = "SELECT MPA_ID, MPA_NAME FROM MPA;";
        return jdbcTemplate.query(sqlQuery, this::assembleMpa);
    }

    private Mpa assembleMpa(ResultSet resultSet, int rowNum) throws SQLException {
        return new Mpa(
                resultSet.getInt("MPA_ID"),
                resultSet.getString("MPA_NAME")
        );
    }
}
