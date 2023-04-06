package ru.yandex.practicum.filmorate.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.storage.film.FilmStorage;
import ru.yandex.practicum.filmorate.storage.user.UserStorage;

import java.util.List;
import java.util.stream.Collectors;

@Service
@Slf4j
public class FilmService {
    FilmStorage filmStorage;

    @Autowired
    public FilmService(FilmStorage filmStorage) {
       this.filmStorage = filmStorage;
    }

    public void addLike(FilmStorage filmStorage, UserStorage userStorage, Long id, Long userId) {
        Film film = filmStorage.getFilmById(id);
        userStorage.getUserById(userId); // Проверка, что пользователь существует
        film.addLike(userId);
        log.info("Лайк фильму {} был добавлен пользователю с ID {}", film, userId);
    }

    public void removeLike(FilmStorage filmStorage, UserStorage userStorage, Long id, Long userId) {
        Film film = filmStorage.getFilmById(id);
        userStorage.getUserById(userId); // Проверка, что пользователь существует
        film.removeLike(userId);
        log.info("Лайк фильму {} был убран пользователем с ID {}", film, userId);
    }

    public List<Film> getPopularFilms(FilmStorage filmStorage, Integer count) {
        return filmStorage.getFilms()
                .stream()
                .sorted((x1, x2) -> x2.getLikes().size() - x1.getLikes().size())
                .limit(count)
                .collect(Collectors.toList());
    }
}
