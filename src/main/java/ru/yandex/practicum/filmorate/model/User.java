package ru.yandex.practicum.filmorate.model;

import lombok.Data;

import javax.validation.constraints.*;
import java.time.LocalDate;

@Data
public class User {
    @NotNull
    private long id;
    @NotBlank
    @Email
    private final String email;
    @NotBlank
    private final String login;
    @NotNull
    private String name;
    @NotNull
    @PastOrPresent
    private final LocalDate birthday;
}
