package ru.yandex.practicum.filmorate.storage.genre;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.filmorate.exception.NoSuchItemException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.Genre;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

@Repository
@Slf4j
public class GenreDbStorage implements GenreStorage {
    private final JdbcTemplate jdbcTemplate;

    @Autowired
    public GenreDbStorage(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    @Override
    public Genre getById(int id) {
        try {
            String sqlQuery = "SELECT GENRE_ID, GENRE_NAME FROM GENRES WHERE GENRE_ID = ?;";
            return jdbcTemplate.queryForObject(sqlQuery, this::assembleGenre, id);
        } catch (EmptyResultDataAccessException e) {
            log.warn("Неверный айди жанра");
            throw new NoSuchItemException("Нет такого жанра");
        }
    }

    @Override
    public Set<Genre> getByFilmId(int filmId) {
        String sqlQuery =
                "SELECT G.GENRE_ID, GENRE_NAME FROM GENRES G" +
                        " JOIN FILM_GENRES FG ON G.GENRE_ID = FG.GENRE_ID WHERE FILM_ID = ?";
        return new LinkedHashSet<>(jdbcTemplate.query(sqlQuery, this::assembleGenre, filmId));
    }

    @Override
    public void addGenresToFilm(Film film) {
        if (film.getGenres() == null) {
            return;
        }
        if (film.getGenres().isEmpty()) {
            film.setGenres(new LinkedHashSet<>());
        }
        final String sqlDelete = "DELETE FROM FILM_GENRES WHERE FILM_ID = ?";
        jdbcTemplate.update(sqlDelete, film.getId());

        final String sqlInsert = "INSERT INTO FILM_GENRES (FILM_ID, GENRE_ID) VALUES (?, ?)";
        for (Genre genre : film.getGenres()) {
            jdbcTemplate.update(sqlInsert, film.getId(), genre.getId());
        }
    }

    @Override
    public List<Genre> getAllGenres() {
        String sqlQuery = "SELECT GENRE_ID, GENRE_NAME FROM GENRES;";
        return jdbcTemplate.query(sqlQuery, this::assembleGenre);
    }

    private Genre assembleGenre(ResultSet resultSet, int rowNum) throws SQLException {
        return new Genre(
                resultSet.getInt("GENRE_ID"),
                resultSet.getString("GENRE_NAME")
        );
    }

}
