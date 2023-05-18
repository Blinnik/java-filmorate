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
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.test.jdbc.JdbcTestUtils;
import ru.yandex.practicum.filmorate.dao.FilmDao;
import ru.yandex.practicum.filmorate.dao.UserDao;
import ru.yandex.practicum.filmorate.exception.FilmNotFoundException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.model.Mpa;
import ru.yandex.practicum.filmorate.model.User;

import java.time.LocalDate;
import java.util.List;
import java.util.Set;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@AutoConfigureTestDatabase
@RequiredArgsConstructor(onConstructor_ = @Autowired)
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class FilmDaoTests {
    FilmDao filmDao;
    UserDao userDao;
    JdbcTemplate jdbcTemplate;

    @AfterEach
    public void beforeEach() {
        JdbcTestUtils.deleteFromTables(jdbcTemplate,"films", "users", "films_genres", "films_likes");
        jdbcTemplate.update("alter table films alter column film_id restart with 1");
        jdbcTemplate.update("alter table users alter column user_id restart with 1");
    }

    @Test
    public void getFilms_shouldReturnEmptyList() {
        List<Film> films = filmDao.getFilms();
        assertEquals(List.of(), films);
    }

    @Test
    public void createFilm_shouldCreateFilmWithEmptyGenresAndLikes() {
        Film film = Film.builder()
                .name("nisi eiusmod")
                .description("adipisicing")
                .releaseDate(LocalDate.of(1967, 3, 25))
                .duration((short) 100)
                .mpa(Mpa.builder().id((short) 1).build())
                .build();

        Film returnedFilm = filmDao.createFilm(film);

        assertThat(returnedFilm)
                .hasFieldOrPropertyWithValue("id", 1L)
                .hasFieldOrPropertyWithValue("name", "nisi eiusmod")
                .hasFieldOrPropertyWithValue("description", "adipisicing")
                .hasFieldOrPropertyWithValue("releaseDate", LocalDate.of(1967, 3, 25))
                .hasFieldOrPropertyWithValue("duration", (short) 100)
                .hasFieldOrPropertyWithValue("mpa.id", (short) 1)
                .hasFieldOrPropertyWithValue("mpa.name", "G");
    }

    @Test
    public void createFilm_shouldCreateFilmWithEmptyLikes() {
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

        Film returnedFilm = filmDao.createFilm(film);

        assertThat(returnedFilm)
                .hasFieldOrPropertyWithValue("id", 1L)
                .hasFieldOrPropertyWithValue("name", "nisi eiusmod")
                .hasFieldOrPropertyWithValue("description", "adipisicing")
                .hasFieldOrPropertyWithValue("releaseDate", LocalDate.of(1967, 3, 25))
                .hasFieldOrPropertyWithValue("duration", (short) 100)
                .hasFieldOrPropertyWithValue("mpa.id", (short) 1)
                .hasFieldOrPropertyWithValue("mpa.name", "G")
                .hasFieldOrPropertyWithValue("genres",
                        Set.of(Genre.builder().id((short) 1).name("Комедия").build(),
                                Genre.builder().id((short) 2).name("Драма").build()));
    }

    @Test
    public void getFilms_shouldReturnListOf2Films() {
        Film film1 = Film.builder()
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

        Film film2 = Film.builder()
                .name("nisi eiusmod")
                .description("adipisicing")
                .releaseDate(LocalDate.of(1967, 3, 25))
                .duration((short) 100)
                .mpa(Mpa.builder().id((short) 1).build())
                .build();

        Film returnedFilm1 = filmDao.createFilm(film1);
        Film returnedFilm2 = filmDao.createFilm(film2);

        List<Film> films = filmDao.getFilms();
        assertEquals(2, films.size());
        assertEquals(List.of(returnedFilm1, returnedFilm2), films);
    }

    @Test
    public void getFilmById_shouldReturnFilmWithCorrectId() {
        Film film1 = Film.builder()
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

        Film film2 = Film.builder()
                .name("nisi eiusmod")
                .description("adipisicing")
                .releaseDate(LocalDate.of(1967, 3, 25))
                .duration((short) 100)
                .mpa(Mpa.builder().id((short) 1).build())
                .build();

        Film createdFilm1 = filmDao.createFilm(film1);
        Film createdFilm2 = filmDao.createFilm(film2);

        assertEquals(createdFilm1, filmDao.getFilmById(1L));
        assertEquals(createdFilm2, filmDao.getFilmById(2L));
    }

    @Test
    public void getFilmById_shouldThrowFilmNotFoundException() {
        assertThrows(FilmNotFoundException.class, () -> filmDao.getFilmById(1L));
    }

    @Test
    public void deleteFilmById_shouldDeleteFilm() {
        Film film1 = Film.builder()
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

        Film createdFilm = filmDao.createFilm(film1);

        assertEquals(createdFilm, filmDao.getFilmById(1L));
        assertTrue(filmDao.deleteFilmById(1L));
        assertThrows(FilmNotFoundException.class, () -> filmDao.getFilmById(1L));
    }

    @Test
    public void deleteFilmById_shouldNotDeleteFilm() {
        assertFalse(filmDao.deleteFilmById(1L));
    }

    @Test
    public void updateFilm_shouldUpdateFilm() {
        Film film = Film.builder()
                .name("nisi eiusmod")
                .description("adipisicing")
                .releaseDate(LocalDate.of(1967, 3, 25))
                .duration((short) 100)
                .mpa(Mpa.builder().id((short) 1).build())
                .build();

        Film createdFilm = filmDao.createFilm(film);

        Film changedFilm = Film.builder()
                // Для обновления фильма нужно сразу задать нужный id
                .id(1L)
                .name("new test name")
                .description("new description")
                .releaseDate(LocalDate.of(1933, 3, 25))
                .duration((short) 200)
                .genres(Set.of(
                        Genre.builder().id((short) 1).build(),
                        Genre.builder().id((short) 2).build()
                ))
                .mpa(Mpa.builder().id((short) 2).build())
                .build();

        filmDao.updateFilm(changedFilm);

        assertNotEquals(createdFilm, filmDao.getFilmById(1L));
        assertEquals(changedFilm, filmDao.getFilmById(1L));
    }

    @Test
    public void updateFilm_shouldThrowNotFoundExceptionWhenIdDoesNotExist() {
        Film changedFilm = Film.builder()
                .id(1L)
                .name("new test name")
                .description("new description")
                .releaseDate(LocalDate.of(1933, 3, 25))
                .duration((short) 200)
                .genres(Set.of(
                        Genre.builder().id((short) 1).build(),
                        Genre.builder().id((short) 2).build()
                ))
                .mpa(Mpa.builder().id((short) 2).build())
                .build();

        assertThrows(FilmNotFoundException.class, () -> filmDao.updateFilm(changedFilm));
    }

    @Test
    public void updateFilm_shouldThrowNotFoundExceptionWhenIdIsNotMentioned() {
        Film changedFilm = Film.builder()
                .name("new test name")
                .description("new description")
                .releaseDate(LocalDate.of(1933, 3, 25))
                .duration((short) 200)
                .genres(Set.of(
                        Genre.builder().id((short) 1).build(),
                        Genre.builder().id((short) 2).build()
                ))
                .mpa(Mpa.builder().id((short) 2).build())
                .build();

        assertThrows(FilmNotFoundException.class, () -> filmDao.updateFilm(changedFilm));
    }

    @Test
    public void addLike_shouldThrowExceptionIfIdDoesNotExist() {
        assertThrows(DataIntegrityViolationException.class, () -> filmDao.addLike(1L, 1L));
    }

    @Test
    public void addLike_shouldAddLikesToFilm() {
        Film film = Film.builder()
                .name("new test name")
                .description("new description")
                .releaseDate(LocalDate.of(1933, 3, 25))
                .duration((short) 200)
                .genres(Set.of(
                        Genre.builder().id((short) 1).build(),
                        Genre.builder().id((short) 2).build()
                ))
                .mpa(Mpa.builder().id((short) 2).build())
                .build();

        User user1 = User.builder()
                .login("dolore")
                .name("Nick Name")
                .email("mail@mail.ru")
                .birthday(LocalDate.of(1946, 8, 20))
                .build();

        User user2 = User.builder()
                .login("dolore2")
                .name("Nick Name2")
                .email("mail2@mail.ru")
                .birthday(LocalDate.of(1946, 8, 20))
                .build();

        filmDao.createFilm(film);
        userDao.createUser(user1);
        userDao.createUser(user2);

        assertTrue(filmDao.addLike(1L, 1L));
        assertTrue(filmDao.addLike(1L, 2L));

        Film filmWithLikes = filmDao.getFilmById(1L);
        assertThat(filmWithLikes)
                .hasFieldOrPropertyWithValue("id", 1L)
                .hasFieldOrPropertyWithValue("likes", Set.of(1L, 2L));
    }

    @Test
    public void removeLike_shouldReturnFalseIfIdDoesNotExist() {
        assertFalse(filmDao.removeLike(1L, 1L));
    }

    @Test
    public void removeLike_shouldRemoveLike() {
        Film film = Film.builder()
                .name("new test name")
                .description("new description")
                .releaseDate(LocalDate.of(1933, 3, 25))
                .duration((short) 200)
                .genres(Set.of(
                        Genre.builder().id((short) 1).build(),
                        Genre.builder().id((short) 2).build()
                ))
                .mpa(Mpa.builder().id((short) 2).build())
                .build();

        User user1 = User.builder()
                .login("dolore")
                .name("Nick Name")
                .email("mail@mail.ru")
                .birthday(LocalDate.of(1946, 8, 20))
                .build();

        User user2 = User.builder()
                .login("dolore2")
                .name("Nick Name2")
                .email("mail2@mail.ru")
                .birthday(LocalDate.of(1946, 8, 20))
                .build();

        filmDao.createFilm(film);
        userDao.createUser(user1);
        userDao.createUser(user2);

        assertTrue(filmDao.addLike(1L, 1L));
        assertTrue(filmDao.addLike(1L, 2L));

        Film filmWithLikes = filmDao.getFilmById(1L);
        assertThat(filmWithLikes)
                .hasFieldOrPropertyWithValue("id", 1L)
                .hasFieldOrPropertyWithValue("likes", Set.of(1L, 2L));

        filmDao.removeLike(1L, 1L);

        Film filmWithLike = filmDao.getFilmById(1L);
        assertThat(filmWithLike)
                .hasFieldOrPropertyWithValue("id", 1L)
                .hasFieldOrPropertyWithValue("likes", Set.of(2L));
    }

    @Test
    public void getLikes_shouldReturnEmptyList() {
        Film film = Film.builder()
                .name("new test name")
                .description("new description")
                .releaseDate(LocalDate.of(1933, 3, 25))
                .duration((short) 200)
                .genres(Set.of(
                        Genre.builder().id((short) 1).build(),
                        Genre.builder().id((short) 2).build()
                ))
                .mpa(Mpa.builder().id((short) 2).build())
                .build();

        filmDao.createFilm(film);
        assertEquals(List.of(), filmDao.getLikes(1L));
    }

    @Test
    public void getLikes_shouldReturnLikes() {
        Film film = Film.builder()
                .name("new test name")
                .description("new description")
                .releaseDate(LocalDate.of(1933, 3, 25))
                .duration((short) 200)
                .genres(Set.of(
                        Genre.builder().id((short) 1).build(),
                        Genre.builder().id((short) 2).build()
                ))
                .mpa(Mpa.builder().id((short) 2).build())
                .build();

        User user1 = User.builder()
                .login("dolore")
                .name("Nick Name")
                .email("mail@mail.ru")
                .birthday(LocalDate.of(1946, 8, 20))
                .build();

        User user2 = User.builder()
                .login("dolore2")
                .name("Nick Name2")
                .email("mail2@mail.ru")
                .birthday(LocalDate.of(1946, 8, 20))
                .build();

        filmDao.createFilm(film);
        userDao.createUser(user1);
        userDao.createUser(user2);

        assertTrue(filmDao.addLike(1L, 1L));
        assertTrue(filmDao.addLike(1L, 2L));

        assertEquals(List.of(1L, 2L), filmDao.getLikes(1L));
    }

    @Test
    public void getPopularFilms_shouldReturnAllFilms() {
        Film film = Film.builder()
                .name("new test name")
                .description("new description")
                .releaseDate(LocalDate.of(1933, 3, 25))
                .duration((short) 200)
                .genres(Set.of(
                        Genre.builder().id((short) 1).build(),
                        Genre.builder().id((short) 2).build()
                ))
                .mpa(Mpa.builder().id((short) 2).build())
                .build();

        Film film2 = Film.builder()
                .name("new test name2")
                .description("new description2")
                .releaseDate(LocalDate.of(1933, 3, 25))
                .duration((short) 100)
                .genres(Set.of(
                        Genre.builder().id((short) 3).build()
                ))
                .mpa(Mpa.builder().id((short) 2).build())
                .build();

        Film createdFilm = filmDao.createFilm(film);
        Film createdFilm2 = filmDao.createFilm(film2);
        // по умолчанию (если параметр null) лимит 10
        assertEquals(List.of(createdFilm, createdFilm2), filmDao.getPopularFilms(10));
    }

    @Test
    public void getPopularFilms_shouldReturn2FilmsOrderedByLikes() {
        Film film = Film.builder()
                .name("new test name")
                .description("new description")
                .releaseDate(LocalDate.of(1933, 3, 25))
                .duration((short) 200)
                .genres(Set.of(
                        Genre.builder().id((short) 1).build(),
                        Genre.builder().id((short) 2).build()
                ))
                .mpa(Mpa.builder().id((short) 2).build())
                .build();

        Film film2 = Film.builder()
                .name("new test name2")
                .description("new description2")
                .releaseDate(LocalDate.of(1933, 3, 25))
                .duration((short) 100)
                .genres(Set.of(
                        Genre.builder().id((short) 3).build()
                ))
                .mpa(Mpa.builder().id((short) 2).build())
                .build();

        Film film3 = Film.builder()
                .name("new test name3")
                .description("new description3")
                .releaseDate(LocalDate.of(1933, 3, 25))
                .duration((short) 100)
                .mpa(Mpa.builder().id((short) 1).build())
                .build();

        User user1 = User.builder()
                .login("dolore")
                .name("Nick Name")
                .email("mail@mail.ru")
                .birthday(LocalDate.of(1946, 8, 20))
                .build();

        User user2 = User.builder()
                .login("dolore2")
                .name("Nick Name2")
                .email("mail2@mail.ru")
                .birthday(LocalDate.of(1946, 8, 20))
                .build();

        User user3 = User.builder()
                .login("dolore3")
                .name("Nick Name3")
                .email("mail3@mail.ru")
                .birthday(LocalDate.of(1946, 8, 20))
                .build();

        filmDao.createFilm(film);
        filmDao.createFilm(film2);
        filmDao.createFilm(film3);
        userDao.createUser(user1);
        userDao.createUser(user2);
        userDao.createUser(user3);

        filmDao.addLike(1L, 1L);

        filmDao.addLike(2L, 1L);
        filmDao.addLike(2L, 2L);

        filmDao.addLike(3L, 1L);
        filmDao.addLike(3L, 2L);
        filmDao.addLike(3L, 3L);

        Film film2With2Likes = filmDao.getFilmById(2L);
        Film film3With3Likes = filmDao.getFilmById(3L);
        // Сначала третий фильм, затем второй
        assertEquals(List.of(film3With3Likes, film2With2Likes), filmDao.getPopularFilms(2));
    }

    /*
    List<Film> getFilms(); 2/2
    Film createFilm(Film film); 2/2
    boolean deleteFilmById(Long id); 2/2
    Film updateFilm(Film film); 3/3
    Film getFilmById(Long id); 2/2
    boolean addLike(Long filmId, Long userId); 2/2
    boolean removeLike(Long filmId, Long userId); 2/2
    List<Long> getLikes(Long filmId) 2/2;
    List<Film> getPopularFilms(Integer limit) 2/2;
    */
}