package ru.yandex.practicum.filmorate.storage.film;

import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.storage.Storage;

import java.util.List;
import java.util.Set;

public interface FilmStorage extends Storage<Film> {
    void addLike(long id, long userId);
    void deleteLike(long id, long userId);
    List<Film> showTopFilms(int size);
    boolean hasUsersLike(long id, long userId);
    Set<Long> getFilmsUserLikes(long id);
}
