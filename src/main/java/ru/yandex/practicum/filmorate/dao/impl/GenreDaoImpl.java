package ru.yandex.practicum.filmorate.dao.impl;

import lombok.AccessLevel;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.dao.GenreDao;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.model.Genre;

import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

@Component
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@Slf4j
public class GenreDaoImpl implements GenreDao {
    JdbcTemplate jdbcTemplate;

    RowMapper<Genre> genreRowMapper = (rs, rowNum) -> Genre.builder()
            .id(rs.getShort("genre_id"))
            .name(rs.getString("name"))
            .build();

    @Autowired
    public GenreDaoImpl(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    @Override
    public Genre getGenreById(Short id) {
        try {
            String sql = "select * from genres where genre_id = ?";
            return jdbcTemplate.queryForObject(sql, genreRowMapper, id);
        } catch (EmptyResultDataAccessException e) {
            log.warn("Жанра с ID={} не существует", id);
            throw new NotFoundException("Жанра с ID=" + id + " не существует");
        }
    }

    @Override
    public List<Genre> getGenres() {
        String sql = "select * from genres";
        return jdbcTemplate.query(sql, genreRowMapper);
    }

    @Override
    public List<Genre> getFilmGenres(Long id) {
        String sql = "select fg.genre_id, g.name " +
                "from films_genres as fg " +
                "inner join genres as g on fg.genre_id = g.genre_id " +
                "where fg.film_id = ?";
        return jdbcTemplate.query(sql, genreRowMapper, id).stream()
                .sorted(Comparator.comparingLong(Genre::getId))
                .collect(Collectors.toList());
    }

    @Override
    public boolean clearFilmGenres(Long filmId) {
        String sql = "delete from films_genres where film_id = ?";
        return jdbcTemplate.update(sql, filmId) > 0;
    }

    @Override
    public boolean addGenreToFilm(Short genreId, Long filmId) {
        String sql = "insert into films_genres (genre_id, film_id) " +
                "values (?, ?)";
        return jdbcTemplate.update(sql, genreId, filmId) > 0;
    }

    @Override
    public String getGenreName(Short genreId) {
        return jdbcTemplate.queryForObject(
                "select name from genres where genre_id = ?",
                String.class,
                genreId
        );
    }
}
