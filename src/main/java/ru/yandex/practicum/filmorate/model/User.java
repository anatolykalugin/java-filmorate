package ru.yandex.practicum.filmorate.model;

import lombok.Data;
import lombok.RequiredArgsConstructor;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.PastOrPresent;
import java.time.LocalDate;
import java.util.HashSet;
import java.util.Set;

@Data
@RequiredArgsConstructor
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
    private final Set<Long> friendList = new HashSet<>();
}
