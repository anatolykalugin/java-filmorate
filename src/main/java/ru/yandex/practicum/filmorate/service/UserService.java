package ru.yandex.practicum.filmorate.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exception.NoSuchItemException;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.user.UserStorage;

import java.util.List;

@Service
@Slf4j
public class UserService implements Serviceable<User> {
    private final UserStorage userStorage;

    @Autowired
    public UserService(UserStorage userStorage) {
        this.userStorage = userStorage;
    }

    @Override
    public List<User> showAll() {
        return userStorage.showAll();
    }

    @Override
    public User addNew(User user) {
        if (user.getLogin().contains(" ")) {
            log.warn("Логин некорректен - " + user.getLogin());
            throw new ValidationException("Ошибка в логине.");
        }
        if (user.getName().isBlank()) {
            log.info("Замена имени на логин");
            user.setName(user.getLogin());
        }
        userStorage.addNew(user);
        log.info("Добавляем юзера успешно");
        return user;
    }

    @Override
    public User update(User user) {
        if (getById(user.getId()) != null) {
            if (user.getLogin().contains(" ")) {
                log.warn("Логин некорректен - " + user.getLogin());
                throw new ValidationException("Ошибка в логине.");
            }
            if (user.getName().isBlank()) {
                log.warn("Замена имени на логин");
                user.setName(user.getLogin());
            }
            userStorage.update(user);
            log.info("Юзер успешно обновлен");
        } else {
            throw new NoSuchItemException("Нет такого юзера");
        }
        return user;
    }

    @Override
    public User getById(long id) {
        if (id <= 0) {
            log.warn("Айди меньше или равен нулю");
            throw new NoSuchItemException("Нет пользователя с таким айди: " + id);
        }
        return userStorage.getById(id);
    }

    @Override
    public void deleteById(long id) {
        if (userStorage.getById(id) != null) {
            userStorage.deleteById(id);
            log.info("Юзера удалили успешно");
        } else {
            log.warn("Запрос на удаление отсутствующего юзера");
            throw new NoSuchItemException("Данный юзер отсутствует");
        }
    }

    public void addFriend(long id, long friendId) {
        if (userStorage.getById(friendId) != null) {
            userStorage.addFriend(id, friendId);
        } else {
            log.warn("Запрос на добавление в друзья отсутствующего юзера");
            throw new NoSuchItemException("Данный юзер отсутствует");
        }
    }

    public void deleteFriend(long id, long friendId) {
        userStorage.deleteFriend(id, friendId);
        log.info("Друга удалили успешно");
    }

    public List<User> showCommonFriends(long id, long friendId) {
        List<User> commonFriendList = userStorage.showCommonFriends(id, friendId);
        log.info("Успешно выведены общие друзья");
        return commonFriendList;
    }

    public List<User> showFriends(long id) {
        List<User> friendList = userStorage.showFriends(id);
        log.info("Список друзей сформирован");
        return friendList;
    }
}
