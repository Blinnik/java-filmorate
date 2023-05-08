package ru.yandex.practicum.filmorate.dao;

import ru.yandex.practicum.filmorate.model.User;

import java.util.List;

public interface UserDao {
    List<User> getUsers();

    User getUserById(Long id);

    User createUser(User user);

    boolean deleteUserById(Long id);

    User updateUser(User user);
}
