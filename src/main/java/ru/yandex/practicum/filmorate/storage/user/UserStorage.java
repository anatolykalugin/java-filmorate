package ru.yandex.practicum.filmorate.storage.user;

import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.Storage;

import java.util.List;

public interface UserStorage extends Storage<User> {
    List<User> showAll();
    User addNew(User user);
    User update(User user);
    User getById(long id);
    void deleteById(long id);
}
