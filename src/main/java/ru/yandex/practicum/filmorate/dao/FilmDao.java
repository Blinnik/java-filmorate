package ru.yandex.practicum.filmorate.dao;

import ru.yandex.practicum.filmorate.model.Film;

import java.util.List;

public interface FilmDao {
    List<Film> getFilms();

    Film createFilm(Film film);

    boolean deleteFilmById(Long id);

    Film updateFilm(Film film);

    Film getFilmById(Long id);

    boolean addLike(Long filmId, Long userId);

    boolean removeLike(Long filmId, Long userId);

    List<Long> getLikes(Long filmId);

    List<Film> getPopularFilms(Integer limit);
}
