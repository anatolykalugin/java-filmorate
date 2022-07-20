package ru.yandex.practicum.filmorate.storage.film;

import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.storage.Storage;

import java.util.List;

public interface FilmStorage extends Storage<Film> {
    Film getById(long id);
    List<Film> showAll();
    Film addNew(Film film);
    Film update(Film film);
    void deleteById(long id);
}
