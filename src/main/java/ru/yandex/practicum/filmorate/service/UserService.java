package ru.yandex.practicum.filmorate.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.user.UserStorage;

import java.util.HashSet;
import java.util.Set;


@Service
@Slf4j
public class UserService {

    public void addFriend(UserStorage userStorage, Long userId, Long friendId) {
        User user = userStorage.getUserById(userId);
        User friend = userStorage.getUserById(friendId);
        user.addFriend(friendId);
        friend.addFriend(userId);
        log.info("Пользователь {} добавил в друзья пользователя {}", user, friend);
    }

    public void deleteFriend(UserStorage userStorage, Long userId, Long friendId) {
        User user = userStorage.getUserById(userId);
        User friend = userStorage.getUserById(friendId);
        user.deleteFriend(friendId);
        friend.deleteFriend(userId);
        log.info("Пользователь {} убрал из друзей пользователя {}", user, friend);
    }

    public Set<User> getFriends(UserStorage userStorage, Long id) {
        User user = userStorage.getUserById(id);
        Set<User> users = new HashSet<>();
        for (Long curUserId : user.getFriends()) {
            users.add(userStorage.getUserById(curUserId));
        }
        return users;
    }

    public Set<User> getCommonFriends(UserStorage userStorage, Long userId, Long friendId) {
        User user = userStorage.getUserById(userId);
        User friend = userStorage.getUserById(friendId);
        Set<User> commonFriends = new HashSet<>();
        for (Long curFriendId : user.getFriends()) {
            if (friend.getFriends().contains(curFriendId)) {
                commonFriends.add(userStorage.getUserById(curFriendId));
            }
        }
        return commonFriends;
    }
}
