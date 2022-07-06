package ru.yandex.practicum.filmorate.controller;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import ru.yandex.practicum.filmorate.exception.ItemAlreadyExistsException;
import ru.yandex.practicum.filmorate.exception.NoSuchItemException;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.Film;

import java.time.LocalDate;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(controllers = FilmController.class)
public class FilmControllerTest {
    @Autowired
    private MockMvc mockMvc;

    private static Gson gson;

    @BeforeAll
    public static void beforeAll() {
        gson = new GsonBuilder()
                .registerTypeAdapter(LocalDate.class, new DateAdapter())
                .create();
    }

    @Test
    @Order(1)
    public void shouldAddANewFilmWithBorderlineValues() throws Exception {
        Film film = new Film("Men", "HorrorFilmHorrorFilmHorrorFilmHorrorFilmHorrorFilm" +
                "HorrorFilmHorrorFilmHorrorFilmHorrorFilmHorrorFilmHorrorFilmHorrorFilmHorrorFilmHorrorFilm" +
                "HorrorFilmHorrorFilmHorrorFilmHorrorFilmHorrorFilmHorrorFilm",
                LocalDate.of(1895, 12, 28),
                1);
        film.setId(1);
        String json = gson.toJson(film);
        MvcResult mvcResult = mockMvc.perform(post("/films")
                .contentType("application/json")
                .content(json))
                .andExpect(status().isOk())
                .andReturn();
        assertEquals(film, gson.fromJson(mvcResult.getResponse().getContentAsString(), Film.class));
        FilmController fc = new FilmController();
        fc.addNewFilm(film);
        Exception exToTest = assertThrows(ItemAlreadyExistsException.class,
                () -> fc.addNewFilm(film));
        assertEquals("Фильм " + film.getName() + " уже добавлен в систему.", exToTest.getMessage());
    }

    @Test
    @Order(2)
    public void shouldFailAddingANewFilmEmptyName() throws Exception {
        Film film = new Film(" ", "Rom-Com", LocalDate.of(2010, 10, 1),
                90);
        film.setId(2);
        String json = gson.toJson(film);
        mockMvc.perform(post("/films")
                        .contentType("application/json")
                        .content(json))
                .andExpect(status().isBadRequest())
                .andReturn();
    }

    @Test
    @Order(3)
    public void shouldFailAddingANewFilmTooLongDescription201Chars() throws Exception {
        Film film = new Film("Sinister", "HorrorFilmHorrorFilmHorrorFilmHorrorFilmHorrorFilm" +
                "HorrorFilmHorrorFilmHorrorFilmHorrorFilmHorrorFilmHorrorFilmHorrorFilmHorrorFilmHorrorFilm" +
                "HorrorFilmHorrorFilmHorrorFilmHorrorFilmHorrorFilmHorrorFilm1",
                LocalDate.of(2012, 10, 30),
                100);
        film.setId(3);
        String json = gson.toJson(film);
        mockMvc.perform(post("/films")
                        .contentType("application/json")
                        .content(json))
                .andExpect(status().isBadRequest())
                .andExpect(result -> assertTrue(result.getResolvedException() instanceof ValidationException))
                .andExpect(result -> assertEquals("Слишком длинное описание",
                        result.getResolvedException().getMessage()))
                .andReturn();
        Exception exToTest = assertThrows(NoSuchItemException.class, () -> new FilmController().updateFilm(film));
        assertEquals("Невозможно изменить данный фильм", exToTest.getMessage());
    }

    @Test
    @Order(4)
    public void shouldFailAddingANewFilmDateTooEarly() throws Exception {
        Film film = new Film("Men", "Horror", LocalDate.of(1895, 12, 27),
                100);
        film.setId(4);
        String json = gson.toJson(film);
        mockMvc.perform(post("/films")
                        .contentType("application/json")
                        .content(json))
                .andExpect(status().isBadRequest())
                .andExpect(result -> assertTrue(result.getResolvedException() instanceof ValidationException))
                .andExpect(result -> assertEquals("Слишком ранняя дата релиза",
                        result.getResolvedException().getMessage()))
                .andReturn();
        Exception exToTest = assertThrows(ValidationException.class, () -> new FilmController().addNewFilm(film));
        assertEquals("Слишком ранняя дата релиза", exToTest.getMessage());
    }

    @Test
    @Order(5)
    public void shouldFailAddingANewFilmNegativeDuration() throws Exception {
        Film film = new Film("Men", "Horror", LocalDate.of(2022, 5, 15),
                -1);
        film.setId(5);
        String json = gson.toJson(film);
        mockMvc.perform(post("/films")
                        .contentType("application/json")
                        .content(json))
                .andExpect(status().isBadRequest())
                .andReturn();
    }

    @Test
    @Order(6)
    public void shouldUpdateAnExistingFilm() throws Exception {
        Film film = new Film("Men", "Horror", LocalDate.of(1895, 12, 28),
                1);
        film.setId(1);
        String json = gson.toJson(film);
        MvcResult mvcResult = mockMvc.perform(MockMvcRequestBuilders.put("/films")
                        .contentType("application/json")
                        .content(json))
                .andExpect(status().isOk())
                .andReturn();
        assertEquals(film, gson.fromJson(mvcResult.getResponse().getContentAsString(), Film.class));
    }

    @Test
    @Order(7)
    public void shouldShowAllFilms() throws Exception {
        MvcResult mvcResult = mockMvc.perform(get("/films")
                        .contentType("application/json"))
                .andExpect(status().isOk())
                .andReturn();
        assertNotNull(gson.fromJson(mvcResult.getResponse().getContentAsString(), List.class));
    }

}
