package ru.yandex.practicum.filmorate.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exception.NoSuchItemException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.storage.genre.GenreStorage;

import java.util.List;
import java.util.Set;

@Slf4j
@Service
public class GenreService {
    private final GenreStorage genreStorage;

    @Autowired
    public GenreService(GenreStorage genreStorage) {
        this.genreStorage = genreStorage;
    }

    public Genre getById(int id) {
        if (genreStorage.getById(id) != null) {
            return genreStorage.getById(id);
        } else {
            throw new NoSuchItemException("Нет жанра с айди " + id);
        }
    }

    public Set<Genre> getByFilmId(int id) {
        return genreStorage.getByFilmId(id);
    }

    public void addGenresToFilm(Film film) {
        genreStorage.addGenresToFilm(film);
    }

    public List<Genre> getAllGenres() {
        List<Genre> genre = genreStorage.getAllGenres();
        log.debug("Load {} genres", genre.size());
        return genre;
    }

}
