package ru.yandex.practicum.filmorate.storage.film;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.RequestBody;
import ru.yandex.practicum.filmorate.exception.ItemAlreadyExistsException;
import ru.yandex.practicum.filmorate.exception.NoSuchItemException;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.Film;

import javax.validation.Valid;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Component
@Slf4j
public class InMemoryFilmStorage implements FilmStorage {

    private final Map<Long, Film> filmMap = new HashMap<>();
    private long id = 1;

    @Override
    public List<Film> showAll() {
        return new ArrayList<>(filmMap.values());
    }

    @Override
    public Film addNew(Film film) {
        log.info("Запрос на добавление нового фильма");
        if (validateFilm(film)) {
            if (filmMap.containsKey(film.getId())) {
                log.warn("Запрос на добавление уже существующего фильма");
                throw new ItemAlreadyExistsException("Фильм " + film.getName() + " уже добавлен в систему.");
            }
            film.setId(id);
            filmMap.put(film.getId(), film);
            id++;
        }
        return film;
    }

    @Override
    public Film update(@Valid @RequestBody Film film) {
        log.info("Запрос на изменение фильма");
        if (filmMap.containsKey(film.getId())) {
            if (validateFilm(film)) {
                filmMap.put(film.getId(), film);
            }
        } else {
            throw new NoSuchItemException("Невозможно изменить данный фильм");
        }
        return film;
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

    @Override
    public Film getById(long id) {
        if (filmMap.containsKey(id)) {
            return filmMap.get(id);
        } else {
            throw new NoSuchItemException("Отсутствует фильм с данным ID");
        }
    }

    @Override
    public void deleteById(long id) {
        if (filmMap.containsKey(id)) {
            filmMap.remove(id);
        } else {
            throw new NoSuchItemException("Отсутствует фильм с данным ID");
        }
    }
}
