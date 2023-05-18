package ru.yandex.practicum.filmorate;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.test.jdbc.JdbcTestUtils;
import ru.yandex.practicum.filmorate.dao.FilmDao;
import ru.yandex.practicum.filmorate.dao.GenreDao;
import ru.yandex.practicum.filmorate.exception.GenreNotFoundException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.model.Mpa;

import java.time.LocalDate;
import java.util.List;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.assertEquals;

@SpringBootTest
@AutoConfigureTestDatabase
@RequiredArgsConstructor(onConstructor_ = @Autowired)
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class GenreDaoTests {
    FilmDao filmDao;
    GenreDao genreDao;
    JdbcTemplate jdbcTemplate;

    @AfterEach
    public void beforeEach() {
        JdbcTestUtils.deleteFromTables(jdbcTemplate,"films", "films_genres");
        jdbcTemplate.update("alter table films alter column film_id restart with 1");
    }

    @Test
    public void getGenreById_shouldGetExistingGenre() {
        assertEquals(Genre.builder().id((short) 1).name("Комедия").build(), genreDao.getGenreById((short) 1));
        assertEquals(Genre.builder().id((short) 2).name("Драма").build(), genreDao.getGenreById((short) 2));
        assertEquals(Genre.builder().id((short) 3).name("Мультфильм").build(), genreDao.getGenreById((short) 3));
        assertEquals(Genre.builder().id((short) 4).name("Триллер").build(), genreDao.getGenreById((short) 4));
        assertEquals(Genre.builder().id((short) 5).name("Документальный").build(), genreDao.getGenreById((short) 5));
        assertEquals(Genre.builder().id((short) 6).name("Боевик").build(), genreDao.getGenreById((short) 6));
    }

    @Test
    public void getGenreById_shouldThrowGenreNotFoundException() {
        assertThrows(GenreNotFoundException.class, () -> genreDao.getGenreById((short) 9999));
    }

    @Test
    public void getGenres_shouldReturnAllGenres() {
        assertEquals(List.of(
                Genre.builder().id((short) 1).name("Комедия").build(),
                Genre.builder().id((short) 2).name("Драма").build(),
                Genre.builder().id((short) 3).name("Мультфильм").build(),
                Genre.builder().id((short) 4).name("Триллер").build(),
                Genre.builder().id((short) 5).name("Документальный").build(),
                Genre.builder().id((short) 6).name("Боевик").build()
        ), genreDao.getGenres());
    }

    @Test
    public void getFilmGenres_shouldReturnEmptyList() {
        Film film = Film.builder()
                .name("nisi eiusmod")
                .description("adipisicing")
                .releaseDate(LocalDate.of(1967, 3, 25))
                .duration((short) 100)
                .mpa(Mpa.builder().id((short) 1).build())
                .build();

        filmDao.createFilm(film);
        assertEquals(List.of(), genreDao.getFilmGenres(1L));
    }

    @Test
    public void getFilmGenres_shouldReturn2Genres() {
        Film film = Film.builder()
                .name("nisi eiusmod")
                .description("adipisicing")
                .releaseDate(LocalDate.of(1967, 3, 25))
                .duration((short) 100)
                .mpa(Mpa.builder().id((short) 1).build())
                .genres(Set.of(
                        Genre.builder().id((short) 1).build(),
                        Genre.builder().id((short) 2).build()
                ))
                .build();

        filmDao.createFilm(film);
        assertEquals(List.of(
                Genre.builder().id((short) 1).name("Комедия").build(),
                Genre.builder().id((short) 2).name("Драма").build()
        ), genreDao.getFilmGenres(1L));
    }

    @Test
    public void clearFilmGenres_shouldClear2Genres() {
        Film film = Film.builder()
                .name("nisi eiusmod")
                .description("adipisicing")
                .releaseDate(LocalDate.of(1967, 3, 25))
                .duration((short) 100)
                .mpa(Mpa.builder().id((short) 1).build())
                .genres(Set.of(
                        Genre.builder().id((short) 1).build(),
                        Genre.builder().id((short) 2).build()
                ))
                .build();

        filmDao.createFilm(film);
        assertEquals(List.of(
                Genre.builder().id((short) 1).name("Комедия").build(),
                Genre.builder().id((short) 2).name("Драма").build()
        ), genreDao.getFilmGenres(1L));
        assertTrue(genreDao.clearFilmGenres(1L));
        assertEquals(List.of(), genreDao.getFilmGenres(1L));
    }

    @Test
    public void clearFilmGenres_shouldReturnFalseIfGenresDoNotExist() {
        Film film = Film.builder()
                .name("nisi eiusmod")
                .description("adipisicing")
                .releaseDate(LocalDate.of(1967, 3, 25))
                .duration((short) 100)
                .mpa(Mpa.builder().id((short) 1).build())
                .build();

        filmDao.createFilm(film);
        assertFalse(genreDao.clearFilmGenres(1L));
    }

    @Test
    public void addGenreToFilm_shouldAddGenreToFilm() {
        Film film = Film.builder()
                .name("nisi eiusmod")
                .description("adipisicing")
                .releaseDate(LocalDate.of(1967, 3, 25))
                .duration((short) 100)
                .mpa(Mpa.builder().id((short) 1).build())
                .build();

        filmDao.createFilm(film);
        assertEquals(List.of(), genreDao.getFilmGenres(1L));
        genreDao.addGenreToFilm((short) 2, 1L);
        assertEquals(List.of(Genre.builder().id((short) 2).name("Драма").build()), genreDao.getFilmGenres(1L));
    }

    @Test
    public void addGenreToFilm_shouldThrowsErrorIfFilmDoesNotExist() {
        assertThrows(DataIntegrityViolationException.class, () -> genreDao.addGenreToFilm((short) 2, 1L));
    }

    @Test
    public void getGenreName_shouldThrowExceptionIfIdDoesNotExist() {
        assertThrows(EmptyResultDataAccessException.class, () -> genreDao.getGenreName((short) 999));
    }

    @Test
    public void getGenreName_shouldGetNameOfGenre() {
        assertEquals("Комедия", genreDao.getGenreName((short) 1));
    }

    /*
    Genre getGenreById(Short id); 2/2
    List<Genre> getGenres(); 1/1
    List<Genre> getFilmGenres(Long id); 2/2
    boolean clearFilmGenres(Long filmId); 2/2
    boolean addGenreToFilm(Short genreId, Long filmId); 2/2
    String getGenreName(Short genreId); 2/2
    */
}