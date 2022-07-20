package ru.yandex.practicum.filmorate.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.service.FilmService;

import javax.validation.Valid;
import java.util.List;

@RestController
@RequestMapping("/films")
@Slf4j
public class FilmController {

    private final FilmService filmService;

    public FilmController(FilmService filmService) {
        this.filmService = filmService;
    }

    @GetMapping(value = "/{id}")
    public Film getFilmById(@PathVariable Long id) {
        log.info("Запрос на поиск фильма по ID");
        return filmService.getById(id);
    }

    @GetMapping
    public List<Film> getAllFilms() {
        log.info("Запрос на выдачу всех фильмов");
        return filmService.showAll();
    }

    @PostMapping
    public Film addNewFilm(@Valid @RequestBody Film film) {
        log.info("Запрос на добавление фильма");
        return filmService.addNew(film);
    }

    @PutMapping
    public Film updateFilm(@Valid @RequestBody Film film) {
        log.info("Запрос на обновление фильма");
        return filmService.update(film);
    }

    @DeleteMapping(value = "/{id}")
    public void deleteFilmById(@PathVariable Long id) {
        log.info("Запрос на удаление фильма по ID");
        filmService.deleteById(id);
    }

    @PutMapping(value = "/{id}/like/{userId}")
    public void addLike(@PathVariable Long id, @PathVariable Long userId) {
        log.info("Запрос на добавление лайка");
        filmService.addLike(id, userId);
    }

    @DeleteMapping(value = "/{id}/like/{userId}")
    public void deleteLike(@PathVariable Long id, @PathVariable Long userId) {
        log.info("Запрос на удаление лайка");
        filmService.deleteLike(id, userId);
    }

    @GetMapping(value = "/popular")
    public List<Film> showTopFilms(@RequestParam(defaultValue = "10") int count) {
        log.info("Запрос на выдачу популярных фильмов");
        return filmService.showTopFilms(count);
    }

}
