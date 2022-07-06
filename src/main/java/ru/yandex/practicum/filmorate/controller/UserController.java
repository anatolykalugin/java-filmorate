package ru.yandex.practicum.filmorate.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.exception.ItemAlreadyExistsException;
import ru.yandex.practicum.filmorate.exception.NoSuchItemException;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.User;

import javax.validation.Valid;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/users")
@Slf4j
public class UserController {
    private final Map<Long, User> userMap = new HashMap<>();
    private long id = 1;

    @GetMapping
    public List<User> showAllUsers() {
        return new ArrayList<>(userMap.values());
    }

    @PostMapping
    public User createUser(@Valid @RequestBody User user) {
        log.info("Запрос на добавление нового юзера");
        if (user.getLogin().contains(" ")) {
            log.warn("Логин некорректен - " + user.getLogin());
            throw new ValidationException("Ошибка в логине.");
        }
        if (user.getName().isBlank()) {
            log.info("Замена имени на логин");
            user.setName(user.getLogin());
        }
        if (userMap.containsKey(user.getId())) {
            log.warn("Запрос на добавление уже существующего юзера");
            throw new ItemAlreadyExistsException("Пользователь с данным ID " +
                    user.getId() + " уже зарегистрирован.");
        }
        user.setId(id);
        userMap.put(user.getId(), user);
        id++;
        return user;
    }

    @PutMapping
    public User put(@Valid @RequestBody User user) {
        log.info("Запрос на изменение юзера");
        if (userMap.containsKey(user.getId())) {
            if (user.getLogin().contains(" ")) {
                log.warn("Логин некорректен - " + user.getLogin());
                throw new ValidationException("Ошибка в логине.");
            }
            if (user.getName().isBlank()) {
                log.warn("Замена имени на логин");
                user.setName(user.getLogin());
            }
            userMap.put(user.getId(), user);
        } else {
            throw new NoSuchItemException("Невозможно изменить данного юзера");
        }
        return user;
    }
}
