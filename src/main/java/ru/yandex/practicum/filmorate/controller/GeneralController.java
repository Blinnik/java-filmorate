package ru.yandex.practicum.filmorate.controller;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.experimental.FieldDefaults;
import org.springframework.web.bind.annotation.RestController;
import ru.yandex.practicum.filmorate.service.film.FilmService;
import ru.yandex.practicum.filmorate.service.user.UserService;
import ru.yandex.practicum.filmorate.storage.film.FilmStorage;
import ru.yandex.practicum.filmorate.storage.user.UserStorage;

// Решил объединить все поля в один контроллер,
// тк FilmController'у необходим UserStorage,
// который проверяет, существует ли пользователь
@RestController
@Getter
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PROTECTED, makeFinal = true)
public class GeneralController {
    FilmStorage filmStorage;
    FilmService filmService;
    UserStorage userStorage;
    UserService userService;
}
