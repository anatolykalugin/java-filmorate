package ru.yandex.practicum.filmorate.service;

import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exception.NoSuchItemException;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.user.UserStorage;

import java.util.ArrayList;
import java.util.List;

@Service
public class UserService {
    private final UserStorage userStorage;

    public UserService(UserStorage userStorage) {
        this.userStorage = userStorage;
    }

    public List<User> showAllUsers() {
        return userStorage.showAllUsers();
    }

    public User createUser(User user) {
        return userStorage.createUser(user);
    }

    public User updateUser(User user) {
        return userStorage.put(user);
    }

    public User getUserById(long id) {
        return userStorage.getUserById(id);
    }

    public void deleteUserById(long id) {
        userStorage.deleteUserById(id);
    }

    public void addFriend(long id, long friendId) {
        if (userStorage.getUserById(friendId) != null) {
            userStorage.getUserById(id).getFriendList().add(friendId);
            userStorage.getUserById(friendId).getFriendList().add(id);
        } else {
            throw new NoSuchItemException("Данный юзер отсутствует");
        }
    }

    public void deleteFriend(long id, long friendId) {
        if (userStorage.getUserById(id).getFriendList().contains(friendId)) {
            userStorage.getUserById(id).getFriendList().remove(friendId);
            userStorage.getUserById(friendId).getFriendList().remove(id);
        } else {
            throw new NoSuchItemException("Данный друг отсутствует");
        }
    }

    public List<User> showCommonFriends(long id, long friendId) {
        List<User> commonFriendList = new ArrayList<>();
        for (Long commonFriendId : userStorage.getUserById(id).getFriendList()) {
            if (userStorage.getUserById(friendId).getFriendList().contains(commonFriendId)) {
                commonFriendList.add(userStorage.getUserById(commonFriendId));
            }
        }
        return commonFriendList;
    }

    public List<User> showFriends(long id) {
        List<User> friendList = new ArrayList<>();
        for (Long friendId : userStorage.getUserById(id).getFriendList()) {
            friendList.add(userStorage.getUserById(friendId));
        }
        return friendList;
    }
}
