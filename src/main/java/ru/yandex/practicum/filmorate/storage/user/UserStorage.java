package ru.yandex.practicum.filmorate.storage.user;

import ru.yandex.practicum.filmorate.model.User;

import java.util.List;

public interface UserStorage {
    List<User> showAllUsers();
    User createUser(User user);
    User put(User user);
    User getUserById(long id);
    void deleteUserById(long id);
}
