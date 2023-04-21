package ru.yandex.practicum.filmorate.storage.film;

import lombok.AccessLevel;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.exception.FilmAlreadyExistsException;
import ru.yandex.practicum.filmorate.exception.FilmNotFoundException;
import ru.yandex.practicum.filmorate.model.Film;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

@Component
@Slf4j
@FieldDefaults(level = AccessLevel.PRIVATE)
public class InMemoryFilmStorage implements FilmStorage {
    final Map<Long, Film> filmsById = new HashMap<>();
    static long lastId = 1;

    @Override
    public Collection<Film> getFilms() {
        return filmsById.values();
    }

    @Override
    public Film createFilm(Film film) {
        if (filmsById.containsValue(film)) {
            log.warn("Фильм {} уже существует", film.getName());
            throw new FilmAlreadyExistsException("Фильм уже существует");
        }

        film.setId(lastId++);
        filmsById.put(film.getId(), film);
        log.info("Фильм был добавлен: {}", film);
        return film;
    }

    @Override
    public void deleteFilmById(Long id) {
        if (!filmsById.containsKey(id)) {
            log.warn("filmId={} не существует", id);
            throw new FilmNotFoundException("Фильма с указанным Id не существует");
        }
        filmsById.remove(id);
        log.info("Фильм ID#{} был удален", id);
    }

    @Override
    public Film updateFilm(Film film) {
        Long filmId = film.getId();
        if (!filmsById.containsKey(filmId)) {
            log.warn("filmId={} не существует", filmId);
            throw new FilmNotFoundException("Фильма с указанным Id не существует");
        }
        filmsById.put(filmId, film);
        log.info("Фильм был обновлен: {}", film);
        return film;
    }

    @Override
    public Film getFilmById(Long id) {
        Film film = filmsById.get(id);
        System.out.println(film);
        if (film == null) {
            throw new FilmNotFoundException("Фильма с id " + id + " не существует");
        }
        return film;
    }
}
