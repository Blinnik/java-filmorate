package ru.yandex.practicum.filmorate.service.user;

import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.user.UserStorage;

import java.util.Set;

public interface UserService {
    void addFriend(UserStorage userStorage, Long userId, Long friendId);

    void deleteFriend(UserStorage userStorage, Long userId, Long friendId);

    Set<User> getFriends(UserStorage userStorage, Long id);

    Set<User> getCommonFriends(UserStorage userStorage, Long userId, Long friendId);
}
