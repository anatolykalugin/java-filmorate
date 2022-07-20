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
public class FilmService implements Serviceable<Film> {
    private final FilmStorage filmStorage;

    public FilmService(FilmStorage filmStorage) {
        this.filmStorage = filmStorage;
    }

    @Override
    public Film getById(long id) {
        return filmStorage.getById(id);
    }

    @Override
    public void deleteById(long id) {
        filmStorage.deleteById(id);
    }

    @Override
    public List<Film> showAll() {
        return filmStorage.showAll();
    }

    @Override
    public Film addNew(Film film) {
        return filmStorage.addNew(film);
    }

    @Override
    public Film update(Film film) {
        return filmStorage.update(film);
    }

    public void addLike(long id, long userId) {
        filmStorage.getById(id).getLikeList().add(userId);
        filmStorage.getById(id).setLikesAmount(filmStorage.getById(id).getLikeList().size());
    }

    public void deleteLike(long id, long userId) {
        if (filmStorage.getById(id).getLikeList().contains(userId)) {
            filmStorage.getById(id).getLikeList().remove(userId);
            filmStorage.getById(id).setLikesAmount(filmStorage.getById(id).getLikeList().size());
        } else {
            log.warn("Запрос на удаление отсутствующего лайка");
            throw new NoSuchItemException("Лайк данного пользователя под этим фильмом отсутствует");
        }
    }

    public List<Film> showTopFilms(int size) {
        Set<Film> sortedByLikes = filmStorage.showAll().stream()
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
