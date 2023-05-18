package ru.yandex.practicum.filmorate.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.service.UserService;
import ru.yandex.practicum.filmorate.dao.UserDao;

import javax.validation.Valid;
import java.util.*;

@RestController
@RequestMapping("/users")
public class UserController {
    UserDao userDao;
    UserService userService;

    @Autowired
    public UserController(UserDao userDao,
                          UserService userService) {
        this.userDao = userDao;
        this.userService = userService;
    }

    @GetMapping
    public Collection<User> getUsers() {
        return userDao.getUsers();
    }

    @GetMapping("/{id}")
    public User getUserById(@PathVariable Long id) {
        return userDao.getUserById(id);
    }

    @PostMapping
    public User createUser(@Valid @RequestBody User user) {
        return userDao.createUser(user);
    }

    @DeleteMapping("/{id}")
    public void deleteUser(@PathVariable Long id) {
        userDao.deleteUserById(id);
    }

    @PutMapping
    public User updateUser(@Valid @RequestBody User user) {
        return userDao.updateUser(user);
    }


    // В данном ТЗ логика этого контроллера меняется
    // friendId добавляет id в друзья
    @PutMapping("/{id}/friends/{friendId}")
    public void addFriend(@PathVariable Long id, @PathVariable Long friendId) {
        userService.addFriendship(friendId, id);
    }

    @DeleteMapping("/{id}/friends/{friendId}")
    public void deleteFriend(@PathVariable Long id, @PathVariable Long friendId) {
        userService.deleteFriendship(id, friendId);
    }

    @GetMapping("/{id}/friends")
    public List<User> getFriends(@PathVariable Long id) {
        return userService.getFriends(id);
    }


    @GetMapping("/{id}/friends/common/{otherId}")
    public List<User> getCommonFriends(@PathVariable Long id, @PathVariable Long otherId) {
        return userService.getCommonFriends(id, otherId);
    }
}
