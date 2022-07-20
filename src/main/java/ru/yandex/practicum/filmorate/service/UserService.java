package ru.yandex.practicum.filmorate.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exception.NoSuchItemException;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.user.UserStorage;

import java.util.ArrayList;
import java.util.List;

@Service
@Slf4j
public class UserService implements Serviceable<User> {
    private final UserStorage userStorage;

    public UserService(UserStorage userStorage) {
        this.userStorage = userStorage;
    }

    @Override
    public List<User> showAll() {
        return userStorage.showAll();
    }

    @Override
    public User addNew(User user) {
        return userStorage.addNew(user);
    }

    @Override
    public User update(User user) {
        return userStorage.update(user);
    }

    @Override
    public User getById(long id) {
        return userStorage.getById(id);
    }

    @Override
    public void deleteById(long id) {
        userStorage.deleteById(id);
    }

    public void addFriend(long id, long friendId) {
        if (userStorage.getById(friendId) != null) {
            userStorage.getById(id).getFriendList().add(friendId);
            userStorage.getById(friendId).getFriendList().add(id);
        } else {
            log.warn("Запрос на добавление в друзья отсутствующего юзера");
            throw new NoSuchItemException("Данный юзер отсутствует");
        }
    }

    public void deleteFriend(long id, long friendId) {
        if (userStorage.getById(id).getFriendList().contains(friendId)) {
            userStorage.getById(id).getFriendList().remove(friendId);
            userStorage.getById(friendId).getFriendList().remove(id);
        } else {
            log.warn("Запрос на удаление из друзей отсутствующего друга");
            throw new NoSuchItemException("Данный друг отсутствует");
        }
    }

    public List<User> showCommonFriends(long id, long friendId) {
        List<User> commonFriendList = new ArrayList<>();
        for (Long commonFriendId : userStorage.getById(id).getFriendList()) {
            if (userStorage.getById(friendId).getFriendList().contains(commonFriendId)) {
                commonFriendList.add(userStorage.getById(commonFriendId));
            }
        }
        return commonFriendList;
    }

    public List<User> showFriends(long id) {
        List<User> friendList = new ArrayList<>();
        for (Long friendId : userStorage.getById(id).getFriendList()) {
            friendList.add(userStorage.getById(friendId));
        }
        return friendList;
    }
}
