package ru.yandex.practicum.filmorate.dao.impl;

import lombok.AccessLevel;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.simple.SimpleJdbcInsert;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.dao.FilmDao;
import ru.yandex.practicum.filmorate.dao.GenreDao;
import ru.yandex.practicum.filmorate.dao.MpaDao;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.model.Mpa;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.*;
import java.util.stream.Collectors;

@Component
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@Slf4j
public class FilmDaoImpl implements FilmDao {
    JdbcTemplate jdbcTemplate;
    GenreDao genreDao;
    MpaDao mpaDao;

    @Autowired
    public FilmDaoImpl(JdbcTemplate jdbcTemplate, GenreDao genreDao, MpaDao mpaDao) {
        this.jdbcTemplate = jdbcTemplate;
        this.genreDao = genreDao;
        this.mpaDao = mpaDao;
    }

    @Override
    public List<Film> getFilms() {
        String sql = "select f.*, m.name as mpa_name " +
                "from films as f " +
                "inner join mpas as m on m.mpa_id = f.mpa_id";

        return jdbcTemplate.query(sql, (rs, rowNum) -> mapRowToFilm(rs));
    }

    @Override
    public Film createFilm(Film film) {
        SimpleJdbcInsert simpleJdbcInsert = new SimpleJdbcInsert(jdbcTemplate)
                .withTableName("films")
                .usingGeneratedKeyColumns("film_id");

        Long filmId = simpleJdbcInsert.executeAndReturnKey(film.toMap()).longValue();
        film.setId(filmId);

        Mpa mpa = film.getMpa();
        if (mpa != null) {
            Short mpaId = mpa.getId();
            mpa.setName(mpaDao.getMpaName(mpaId));
        }

        Set<Genre> genres = film.getGenres();
        if (genres != null) {
            addGenres(film, genres);
        } else {
            genres = new HashSet<>();
            film.setGenres(genres);
        }

        Set<Long> likes = new HashSet<>();
        film.setLikes(likes);

        log.info("Был создан новый фильм {}", film);
        return film;
    }

    @Override
    public boolean deleteFilmById(Long id) {
        String sql = "delete from films where film_id = ?";
        if (jdbcTemplate.update(sql, id) > 0) {
            log.info("Фильм с ID={} был успешно удален", id);
            return true;
        }
        log.warn("Не удалось удалить фильм с ID={}", id);
        return false;
    }

    @Override
    public Film updateFilm(Film film) {
        String sql = "update films " +
                "set name = ?," +
                "description = ?," +
                "release_date = ?," +
                "duration = ?," +
                "mpa_id = ?" +
                "where film_id = ?";
        Long filmId = film.getId();
        Short mpaId = film.getMpa().getId();

        if (jdbcTemplate.update(sql, film.getName(), film.getDescription(), film.getReleaseDate(),
                film.getDuration(), mpaId, filmId) > 0) {
            film.getMpa().setName(mpaDao.getMpaName(mpaId));

            Set<Genre> genres = film.getGenres();
            if (genres != null) {
                genreDao.clearFilmGenres(filmId);
                addGenres(film, genres);
            } else {
                genres = new HashSet<>();
                film.setGenres(genres);
            }

            List<Long> likes = getLikes(filmId);
            if (likes != null) {
                addLikesToFilmObject(film, likes);
            }

            log.info("Фильм с ID={} обновлен", film.getId());
            return film;
        }
        log.warn("Фильм с ID={} не найден", film.getId());
        throw new NotFoundException("Фильма с ID=" + filmId + " не существует");
    }

    @Override
    public Film getFilmById(Long id) {
        String sql = "select f.*, m.name as mpa_name " +
                "from films as f " +
                "inner join mpas as m on m.mpa_id = f.mpa_id " +
                "where film_id = ?";
        try {
            return jdbcTemplate.queryForObject(sql, (rs, rowNum) -> mapRowToFilm(rs), id);
        } catch (EmptyResultDataAccessException e) {
            log.warn("Фильм с ID={} не найден", id);
            throw new NotFoundException("Фильма с ID=" + id + " не существует");
        }
    }

    @Override
    public boolean addLike(Long filmId, Long userId) {
        String sql = "insert into films_likes (user_id, film_id) " +
                "values (?, ?)";
        if (jdbcTemplate.update(sql, userId, filmId) > 0) {
            log.info("Пользователь с ID={} поставил лайк фильму с ID={}", userId, filmId);
            return true;
        }
        log.warn("Пользователю с ID={} не удалось поставить лайк фильму с ID={}", userId, filmId);
        return false;
    }

    @Override
    public boolean removeLike(Long filmId, Long userId) {
        String sql = "delete from films_likes " +
                "where user_id = ? and film_id = ?";
        if (jdbcTemplate.update(sql, userId, filmId) > 0) {
            log.info("Пользователь с ID={} удалил лайк фильму с ID={}", userId, filmId);
            return true;
        }
        log.warn("Пользователю с ID={} не удалось удалить лайк фильму с ID={}", userId, filmId);
        return false;
    }

    @Override
    public List<Long> getLikes(Long filmId) {
        String sql = "select user_id from films_likes where film_id = ?";
        return jdbcTemplate.query(sql, (rs, rowNum) -> rs.getLong("user_id"), filmId);
    }

    @Override
    public List<Film> getPopularFilms(Integer count) {
        String sql = "select f.*, m.name as mpa_name " +
                "from films as f " +
                "inner join mpas as m on m.mpa_id = f.mpa_id " +
                "left join " +
                "(select film_id, count(*) as like_count from films_likes group by film_id) as fl " +
                "on fl.film_id = f.film_id " +
                "order by like_count desc, film_id " +
                "limit ?";

        return jdbcTemplate.query(sql, (rs, rowNum) -> mapRowToFilm(rs), count);
    }

    private void addLikesToFilmObject(Film film, List<Long> likes) {
        film.setLikes(likes.stream()
                .sorted(Comparator.naturalOrder())
                .collect(Collectors.toCollection(LinkedHashSet::new)));
    }

    // Adds genres to the database, and also adds the genre name to the movie object
    private void addGenres(Film film, Set<Genre> genres) {
        genres.forEach(genre -> {
            Short genreId = genre.getId();
            genre.setName(genreDao.getGenreName(genreId));
            genreDao.addGenreToFilm(genreId, film.getId());
        });

        film.setGenres(genres.stream()
                .sorted(Comparator.comparing(Genre::getId))
                .collect(Collectors.toCollection(LinkedHashSet::new)));
    }

    private Film mapRowToFilm(ResultSet rs) throws SQLException {
        Long id = rs.getLong("film_id");
        String name = rs.getString("name");
        String description = rs.getString("description");
        LocalDate releaseDate = rs.getDate("release_date").toLocalDate();
        Short duration = rs.getShort("duration");

        Mpa mpa = Mpa.builder()
                .id(rs.getShort("mpa_id"))
                .name(rs.getString("mpa_name"))
                .build();

        Set<Genre> genres = new HashSet<>(genreDao.getFilmGenres(id));
        Set<Long> likes = new HashSet<>(getLikes(id));

        return Film.builder()
                .id(id)
                .name(name)
                .description(description)
                .releaseDate(releaseDate)
                .duration(duration)
                .mpa(mpa)
                .genres(genres)
                .likes(likes)
                .build();
    }
}
