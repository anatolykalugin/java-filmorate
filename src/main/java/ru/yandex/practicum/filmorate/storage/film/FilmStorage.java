package ru.yandex.practicum.filmorate.storage.film;

import ru.yandex.practicum.filmorate.model.Film;

import java.util.List;

public interface FilmStorage {
    Film getFilmById(long id);
    List<Film> showAllFilms();
    Film addNewFilm(Film film);
    Film updateFilm(Film film);
    void deleteFilmById(long id);
}
