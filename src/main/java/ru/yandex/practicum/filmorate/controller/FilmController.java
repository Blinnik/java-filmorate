package ru.yandex.practicum.filmorate.controller;

import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.exception.*;
import ru.yandex.practicum.filmorate.model.Film;

import java.util.*;

@RestController
@RequestMapping("/films")
@Slf4j
public class FilmController {
    Map<Integer, Film> filmsById = new HashMap<>();
    private static int lastId = 1;

    @GetMapping
    public Collection<Film> getFilms() {
        return filmsById.values();
    }

    @PostMapping
    public Film createFilm(@Valid @RequestBody Film film) {
        if (filmsById.containsValue(film)) {
            log.warn("Фильм {} уже существует", film.getName());
            throw new FilmAlreadyExistsException("Фильм уже существует");
        }

        film.setId(lastId++);
        filmsById.put(film.getId(), film);
        log.info("Фильм был добавлен: {}", film);
        return film;
    }

    @PutMapping
    public Film updateFilm(@Valid @RequestBody Film film) {
        Integer filmId = film.getId();
        if (!filmsById.containsKey(filmId)) {
            log.warn("filmId={} не существует", filmId);
            throw new FilmNotFoundException("Фильма с указанным Id не существует");
        }
        filmsById.put(filmId, film);
        log.info("Фильм был обновлен: {}", film);
        return film;
    }
}
