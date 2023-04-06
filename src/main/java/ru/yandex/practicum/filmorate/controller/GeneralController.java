package ru.yandex.practicum.filmorate.controller;

import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.web.bind.annotation.RestController;
import ru.yandex.practicum.filmorate.service.FilmService;
import ru.yandex.practicum.filmorate.service.UserService;
import ru.yandex.practicum.filmorate.storage.film.FilmStorage;
import ru.yandex.practicum.filmorate.storage.user.UserStorage;

// Решил объединить все поля в один контроллер,
// тк FilmController'у необходим UserStorage,
// который проверяет, существует ли пользователь
@RestController
@Getter
@AllArgsConstructor
public class GeneralController {
    FilmStorage filmStorage;
    FilmService filmService;
    UserStorage userStorage;
    UserService userService;
}
