package ru.yandex.practicum.filmorate.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exception.NoSuchItemException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.storage.film.FilmStorage;

import java.util.*;
import java.util.stream.Collectors;

@Service
@Slf4j
public class FilmService {
    private final FilmStorage filmStorage;

    public FilmService(FilmStorage filmStorage) {
        this.filmStorage = filmStorage;
    }

    public Film getFilmById(long id) {
        return filmStorage.getFilmById(id);
    }

    public void deleteFilmById(long id) {
        filmStorage.deleteFilmById(id);
    }

    public List<Film> showAllFilms() {
        return filmStorage.showAllFilms();
    }

    public Film addNewFilm(Film film) {
        return filmStorage.addNewFilm(film);
    }

    public Film updateFilm(Film film) {
        return filmStorage.updateFilm(film);
    }

    public void addLike(long id, long userId) {
        filmStorage.getFilmById(id).getLikeList().add(userId);
        filmStorage.getFilmById(id).setLikesAmount(filmStorage.getFilmById(id).getLikeList().size());
    }

    public void deleteLike(long id, long userId) {
        if (filmStorage.getFilmById(id).getLikeList().contains(userId)) {
            filmStorage.getFilmById(id).getLikeList().remove(userId);
            filmStorage.getFilmById(id).setLikesAmount(filmStorage.getFilmById(id).getLikeList().size());
        } else {
            log.warn("Запрос на удаление отсутствующего лайка");
            throw new NoSuchItemException("Лайк данного пользователя под этим фильмом отсутствует");
        }
    }

    public List<Film> showTopFilms(int size) {
        Set<Film> sortedByLikes = filmStorage.showAllFilms().stream()
                .sorted(Comparator.comparing(Film::getLikesAmount).reversed())
                .collect(Collectors.toCollection(LinkedHashSet::new));
        List<Film> topFilms = new ArrayList<>();
        Iterator<Film> iter = sortedByLikes.iterator();
        for (int n = 0; n < size && iter.hasNext(); n++) {
            Film film = iter.next();
            topFilms.add(film);
        }
        return topFilms;
    }

}
