package ru.yandex.practicum.filmorate.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exception.ItemAlreadyExistsException;
import ru.yandex.practicum.filmorate.exception.NoSuchItemException;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.storage.film.FilmStorage;

import java.time.LocalDate;
import java.util.List;

@Service
@Slf4j
public class FilmService implements Serviceable<Film> {
    private final FilmStorage filmStorage;
    private final GenreService genreService;

    public FilmService(FilmStorage filmStorage, GenreService genreService) {
        this.filmStorage = filmStorage;
        this.genreService = genreService;
    }

    @Override
    public Film getById(long id) {
        if (id <= 0) {
            log.warn("Айди меньше или равен нулю");
            throw new NoSuchItemException("Нет фильма с таким айди: " + id);
        }
        log.info("Успешно найден фильм");
        return filmStorage.getById(id);
    }

    @Override
    public void deleteById(long id) {
        if (filmStorage.getById(id) != null) {
            filmStorage.deleteById(id);
            log.info("Фильм удалили успешно");
        } else {
            log.warn("Запрос на удаление отсутствующего фильма");
            throw new NoSuchItemException("Данный фильм отсутствует");
        }
    }

    @Override
    public List<Film> showAll() {
        return filmStorage.showAll();
    }

    @Override
    public Film addNew(Film film) {
        if (validateFilm(film)) {
            filmStorage.addNew(film);
        }
        if (film.getGenres() != null && film.getGenres().size() > 0) {
            genreService.addGenresToFilm(film);
        }
        return film;
    }

    @Override
    public Film update(Film film) {
        if (validateFilm(film)) {
            if (film.getId() <= 0) {
                log.warn("Айди меньше или равен нулю");
                throw new NoSuchItemException("Некорректный айди фильма");
            }
            genreService.addGenresToFilm(film);
            filmStorage.update(film);
        }
        return film;
    }

    public void addLike(long id, long userId) {
        if (filmStorage.hasUsersLike(id, userId)) {
            log.warn("Уже есть лайк от пользователя");
            throw new ItemAlreadyExistsException("Уже есть лайк от этого пользователя");
        } else {
            filmStorage.addLike(id, userId);
            log.info("Лайк успешно добавлен");
        }
    }

    public void deleteLike(long id, long userId) {
        if (filmStorage.hasUsersLike(id, userId)) {
            filmStorage.deleteLike(id, userId);
            log.info("Лайк успешно удален");
        } else {
            log.warn("Нет лайка от пользователя");
            throw new NoSuchItemException("Нет лайков от этого пользователя");
        }
    }

    public List<Film> showTopFilms(int size) {
        return filmStorage.showTopFilms(size);
    }

    private boolean validateFilm(Film film) {
        if (film.getDescription().length() > 200) {
            log.warn("Неправильная длина описания");
            throw new ValidationException("Слишком длинное описание");
        }
        if (film.getReleaseDate().isBefore(LocalDate.of(1895, 12, 28))) {
            log.warn("Неправильная дата релиза");
            throw new ValidationException("Слишком ранняя дата релиза");
        }
        return true;
    }
}
