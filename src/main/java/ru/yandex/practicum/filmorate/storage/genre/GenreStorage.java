package ru.yandex.practicum.filmorate.storage.genre;

import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.Genre;

import java.util.List;
import java.util.Set;

public interface GenreStorage {
    Genre getById(int id);
    List<Genre> getAllGenres();
    Set<Genre> getByFilmId(int filmId);
    void addGenresToFilm(Film film);
}
