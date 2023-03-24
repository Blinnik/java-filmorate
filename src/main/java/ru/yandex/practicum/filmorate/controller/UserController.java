package ru.yandex.practicum.filmorate.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.exception.UserAlreadyExistsException;
import ru.yandex.practicum.filmorate.exception.UserNotFoundException;
import ru.yandex.practicum.filmorate.model.User;

import javax.validation.Valid;
import java.util.*;

@RestController
@RequestMapping("/users")
@Slf4j
public class UserController {
    Map<Integer, User> usersById = new HashMap<>();
    private static int lastId = 1;
    @GetMapping
    public Collection<User> getUsers() {
        return usersById.values();
    }

    @PostMapping
    public User createUser(@Valid @RequestBody User user) {
        if (usersById.containsValue(user)) {
            log.warn("Пользователь {} уже существует", user);
            throw new UserAlreadyExistsException("Пользователь уже существует");
        }
        setNameIfNull(user);

        user.setId(lastId++);
        usersById.put(user.getId(), user);
        log.info("Пользователь был добавлен: {}", user);
        return user;
    }

    @PutMapping
    public User updateUser(@Valid @RequestBody User user) {
        setNameIfNull(user);
        Integer userId = user.getId();
        if (!usersById.containsKey(userId)) {
            log.warn("userId={} не существует", userId);
            throw new UserNotFoundException("Пользователя с указанным Id не существует");
        }
        usersById.put(userId, user);
        log.info("Пользователь был обновлен: {}", user);
        return user;
    }

    private void setNameIfNull(User user) {
        if (user.getName() == null) {
            user.setName(user.getLogin());
        }
    }
}
