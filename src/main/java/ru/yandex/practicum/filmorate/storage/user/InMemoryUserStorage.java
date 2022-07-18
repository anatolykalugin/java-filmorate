package ru.yandex.practicum.filmorate.storage.user;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.exception.ItemAlreadyExistsException;
import ru.yandex.practicum.filmorate.exception.NoSuchItemException;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.User;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Component
@Slf4j
public class InMemoryUserStorage implements UserStorage {

    private final Map<Long, User> userMap = new HashMap<>();
    private long id = 1;

    @Override
    public List<User> showAllUsers() {
        return new ArrayList<>(userMap.values());
    }

    @Override
    public User createUser(User user) {
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

    @Override
    public User put(User user) {
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

    @Override
    public User getUserById(long id) {
        if (userMap.containsKey(id)) {
            return userMap.get(id);
        } else {
            throw new NoSuchItemException("Отсутствует пользователь с данным ID");
        }
    }

    @Override
    public void deleteUserById(long id) {
        if (userMap.containsKey(id)) {
            userMap.remove(id);
        } else {
            throw new NoSuchItemException("Отсутствует пользователь с данным ID");
        }
    }
}
