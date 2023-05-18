package ru.yandex.practicum.filmorate.dao;

import ru.yandex.practicum.filmorate.model.Genre;

import java.util.List;

public interface GenreDao {
    Genre getGenreById(Short id);

    List<Genre> getGenres();

    List<Genre> getFilmGenres(Long id);

    boolean clearFilmGenres(Long filmId);

    boolean addGenreToFilm(Short genreId, Long filmId);

    String getGenreName(Short genreId);
}
