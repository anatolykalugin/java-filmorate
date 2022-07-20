package ru.yandex.practicum.filmorate.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.service.UserService;

import javax.validation.Valid;
import java.util.List;

@RestController
@RequestMapping("/users")
@Slf4j
public class UserController {

    private final UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }

    @GetMapping(value = "/{id}")
    public User getUserById(@PathVariable Long id) {
        log.info("Запрос на поиск пользователя по ID");
        return userService.getById(id);
    }

    @GetMapping
    public List<User> getAllUsers() {
        log.info("Запрос на поиск пользователя по ID");
        return userService.showAll();
    }

    @PostMapping
    public User addNewUser(@Valid @RequestBody User user) {
        log.info("Запрос на добавление пользователя");
        return userService.addNew(user);
    }

    @PutMapping
    public User updateUser(@Valid @RequestBody User user) {
        log.info("Запрос на обновление пользователя");
        return userService.update(user);
    }

    @DeleteMapping(value = "/{id}")
    public void deleteUser(@PathVariable Long id) {
        log.info("Запрос на удаление пользователя по ID");
        userService.deleteById(id);
    }

    @PutMapping(value = "/{id}/friends/{friendId}")
    public void addToFriends(@PathVariable Long id, @PathVariable Long friendId) {
        log.info("Запрос на добавление друга");
        userService.addFriend(id, friendId);
    }

    @DeleteMapping(value = "/{id}/friends/{friendId}")
    public void deleteFriend(@PathVariable Long id, @PathVariable Long friendId) {
        log.info("Запрос на удаление друга");
        userService.deleteFriend(id, friendId);
    }

    @GetMapping(value = "/{id}/friends")
    public List<User> showFriends(@PathVariable Long id) {
        log.info("Запрос на выдачу всех друзей");
        return userService.showFriends(id);
    }

    @GetMapping(value = "/{id}/friends/common/{otherId}")
    public List<User> showCommonFriends(@PathVariable Long id, @PathVariable Long otherId) {
        log.info("Запрос на выдачу общих друзей");
        return userService.showCommonFriends(id, otherId);
    }

}
