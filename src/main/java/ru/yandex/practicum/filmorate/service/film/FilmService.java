package ru.yandex.practicum.filmorate.service.film;

import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.storage.film.FilmStorage;
import ru.yandex.practicum.filmorate.storage.user.UserStorage;

import java.util.List;

public interface FilmService {
    void addLike(FilmStorage filmStorage, UserStorage userStorage, Long id, Long userId);

    void removeLike(FilmStorage filmStorage, UserStorage userStorage, Long id, Long userId);

    List<Film> getPopularFilms(FilmStorage filmStorage, Integer count);
}
