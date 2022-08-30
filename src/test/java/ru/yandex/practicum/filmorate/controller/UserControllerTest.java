package ru.yandex.practicum.filmorate.controller;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.User;

import java.time.LocalDate;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@AutoConfigureTestDatabase
@AutoConfigureMockMvc
@SpringBootTest
public class UserControllerTest {
    @Autowired
    private MockMvc mockMvc;
    private final Gson gson = new GsonBuilder()
            .registerTypeAdapter(LocalDate.class, new DateAdapter())
            .create();

    @Test
    @Order(1)
    public void shouldCreateANewUserWithBorderlineValues() throws Exception {
        User user = new User(1L, "dolin", "a24@mail.ru", "Anton",
                LocalDate.of(1970, 1, 1));
        String json = gson.toJson(user);
        MvcResult result = mockMvc.perform(post("/users")
                        .contentType("application/json")
                        .content(json))
                .andExpect(status().isOk())
                .andReturn();
        assertEquals(user, gson.fromJson(result.getResponse().getContentAsString(), User.class));
    }

    @Test
    @Order(2)
    public void shouldFailCreatingANewUserWrongEmail() throws Exception {
        User user = new User(2L, "12kdk12", "mailruyandexru", "Nick",
                LocalDate.of(2010, 2, 2));
        String json = gson.toJson(user);
        mockMvc.perform(post("/users")
                        .contentType("application/json")
                        .content(json))
                .andExpect(status().isBadRequest())
                .andReturn();
    }

    @Test
    @Order(3)
    public void shouldUpdateAnExistingUser() throws Exception {
        User user = new User(1L, "dolin2", "a24@mail.ru", "Anton2",
                LocalDate.of(1970, 1, 1));
        String json = gson.toJson(user);
        MvcResult result = mockMvc.perform(put("/users")
                        .contentType("application/json")
                        .content(json))
                .andExpect(status().isOk())
                .andReturn();
        assertEquals("Anton2", gson.fromJson(result.getResponse().getContentAsString(), User.class).getName());
    }

    @Test
    @Order(4)
    public void shouldFailCreatingANewUserWrongLogin() throws Exception {
        User user = new User(3L, "pudge mid", "test@yandex.ru", "Greg",
                LocalDate.of(2010, 2, 2));
        user.setName("xXx_DeM0lIsH3R_xXx");
        user.setId(3);
        String json = gson.toJson(user);
        mockMvc.perform(post("/users")
                        .contentType("application/json")
                        .content(json))
                .andExpect(status().isBadRequest())
                .andExpect(result -> assertTrue(result.getResolvedException() instanceof ValidationException))
                .andExpect(result -> assertEquals("Ошибка в логине.",
                        result.getResolvedException().getMessage()))
                .andReturn();
    }

    @Test
    @Order(5)
    public void shouldFailCreatingANewUserWrongBirthday() throws Exception {
        User user = new User(4L, "damndaniel", "ok@gmail.com", "Dan",
                LocalDate.of(2023, 2, 2));
        user.setName("xXx_DeM0lIsH3R_xXx");
        user.setId(4);
        String json = gson.toJson(user);
        mockMvc.perform(post("/users")
                        .contentType("application/json")
                        .content(json))
                .andExpect(status().isBadRequest())
                .andReturn();
    }

    @Test
    @Order(6)
    public void shouldShowAllUsers() throws Exception {
        MvcResult result = mockMvc.perform(get("/users")
                        .contentType("*/*"))
                .andExpect(status().isOk())
                .andReturn();
        assertNotNull(gson.fromJson(result.getResponse().getContentAsString(), List.class));
    }

    @Test
    @Order(7)
    public void shouldThrow404() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.get("/users/9")
                        .contentType("application/json"))
                .andExpect(status().isNotFound())
                .andReturn();
    }
}
