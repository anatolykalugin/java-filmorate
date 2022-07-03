package ru.yandex.practicum.filmorate.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
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

@RestController
@RequestMapping("/films")
@Slf4j
public class FilmController {
    private final Map<Long, Film> filmMap = new HashMap<>();
    private long id = 1;

    @GetMapping
    public List<Film> showAllFilms() {
        return new ArrayList<>(filmMap.values());
    }

    @PostMapping
    public Film addNewFilm(@Valid @RequestBody Film film) {
        log.info("Запрос на добавление нового фильма");
        if (film.getDescription().length() > 200) {
            log.warn("Описание слишком длинное");
            throw new ValidationException("Описание фильма должно быть короче 200 символов.");
        }
        if (film.getReleaseDate().isBefore(LocalDate.of(1895, 12, 28))) {
            log.warn("Дата релиза некорректная - " + film.getReleaseDate());
            throw new ValidationException("Неверная дата релиза.");
        }
        if (filmMap.containsKey(film.getId())) {
            log.warn("Запрос на добавление уже существующего фильма");
            throw new ItemAlreadyExistsException("Фильм " + film.getName() + " уже добавлен в систему.");
        }
        film.setId(id);
        filmMap.put(film.getId(), film);
        id++;
        return film;
    }

    @PutMapping
    public Film updateFilm(@Valid @RequestBody Film film) {
        log.info("Запрос на изменение фильма");
        if (filmMap.containsKey(film.getId())) {
            if (film.getDescription().length() > 200) {
                log.warn("Описание слишком длинное");
                throw new ValidationException("Описание фильма должно быть короче 200 символов.");
            }
            if (film.getReleaseDate().isBefore(LocalDate.of(1895, 12, 28))) {
                log.warn("Дата релиза некорректная - " + film.getReleaseDate());
                throw new ValidationException("Неверная дата релиза.");
            }
            filmMap.put(film.getId(), film);
        } else {
            throw new NoSuchItemException("Невозможно изменить данный фильм");
        }
        return film;
    }
}
