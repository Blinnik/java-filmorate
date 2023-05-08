package ru.yandex.practicum.filmorate;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.test.jdbc.JdbcTestUtils;
import ru.yandex.practicum.filmorate.dao.UserDao;
import ru.yandex.practicum.filmorate.exception.UserNotFoundException;
import ru.yandex.practicum.filmorate.model.User;

import java.time.LocalDate;
import java.util.List;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.junit.jupiter.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.assertFalse;

@SpringBootTest
@AutoConfigureTestDatabase
@RequiredArgsConstructor(onConstructor_ = @Autowired)
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class UserDaoTests {
    UserDao userDao;
    JdbcTemplate jdbcTemplate;

    @AfterEach
    public void beforeEach() {
        JdbcTestUtils.deleteFromTables(jdbcTemplate, "users");
        jdbcTemplate.update("alter table users alter column user_id restart with 1");
    }

    @Test
    public void getUsers_shouldReturnEmptyList() {
        List<User> users = userDao.getUsers();
        assertEquals(List.of(), users);
    }

    @Test
    public void createUser_shouldCreateUser() {
        User user = User.builder()
                .login("dolore")
                .name("Nick Name")
                .email("mail@mail.ru")
                .birthday(LocalDate.of(1946, 8, 20))
                .build();

        User createdUser = userDao.createUser(user);

        assertThat(createdUser)
                .hasFieldOrPropertyWithValue("id", 1L)
                .hasFieldOrPropertyWithValue("login", "dolore")
                .hasFieldOrPropertyWithValue("name", "Nick Name")
                .hasFieldOrPropertyWithValue("email", "mail@mail.ru")
                .hasFieldOrPropertyWithValue("birthday", LocalDate.of(1946, 8, 20));
    }

    @Test
    public void createUser_shouldCreateUserWithEmptyName() {
        User user = User.builder()
                .login("dolore")
                .name("")
                .email("mail@mail.ru")
                .birthday(LocalDate.of(1946, 8, 20))
                .build();

        User createdUser = userDao.createUser(user);

        assertThat(createdUser)
                .hasFieldOrPropertyWithValue("id", 1L)
                .hasFieldOrPropertyWithValue("login", "dolore")
                .hasFieldOrPropertyWithValue("name", "dolore")
                .hasFieldOrPropertyWithValue("email", "mail@mail.ru")
                .hasFieldOrPropertyWithValue("birthday", LocalDate.of(1946, 8, 20));
    }

    @Test
    public void getUsers_shouldReturnListOf2Users() {
        User user1 = User.builder()
                .login("dolore")
                .name("check")
                .email("mail@mail.ru")
                .birthday(LocalDate.of(1946, 8, 20))
                .build();

        User user2 = User.builder()
                .login("random")
                .name("")
                .email("mail@mail.ru")
                .birthday(LocalDate.of(1946, 8, 20))
                .build();

        User createdUser1 = userDao.createUser(user1);
        User createdUser2 = userDao.createUser(user2);

        List<User> users = userDao.getUsers();
        assertEquals(2, users.size());
        assertEquals(List.of(createdUser1, createdUser2), users);
    }

    @Test
    public void getUserById_shouldReturnUserWithCorrectId() {
        User user1 = User.builder()
                .login("dolore")
                .name("check")
                .email("mail@mail.ru")
                .birthday(LocalDate.of(1946, 8, 20))
                .build();

        User user2 = User.builder()
                .login("random")
                .name("")
                .email("mail@mail.ru")
                .birthday(LocalDate.of(1946, 8, 20))
                .build();

        User createdUser1 = userDao.createUser(user1);
        User createdUser2 = userDao.createUser(user2);

        assertEquals(createdUser1, userDao.getUserById(1L));
        assertEquals(createdUser2, userDao.getUserById(2L));
    }

    @Test
    public void getUserById_shouldThrowUserNotFoundException() {
        assertThrows(UserNotFoundException.class, () -> userDao.getUserById(1L));
    }

    @Test
    public void deleteFilmById_shouldDeleteFilm() {
        User user = User.builder()
                .login("dolore")
                .name("check")
                .email("mail@mail.ru")
                .birthday(LocalDate.of(1946, 8, 20))
                .build();

        User createdUser = userDao.createUser(user);

        assertEquals(createdUser, userDao.getUserById(1L));
        assertTrue(userDao.deleteUserById(1L));
        assertThrows(UserNotFoundException.class, () -> userDao.getUserById(1L));
    }

    @Test
    public void deleteUserById_shouldNotDeleteUser() {
        assertFalse(userDao.deleteUserById(1L));
    }

    @Test
    public void updateUser_shouldUpdateUser() {
        User user = User.builder()
                .login("dolore")
                .name("check")
                .email("mail@mail.ru")
                .birthday(LocalDate.of(1946, 8, 20))
                .build();

        User createdUser = userDao.createUser(user);

        User changedUser = User.builder()
                // Для обновления пользователя нужно сразу задать нужный id
                .id(1L)
                .login("login")
                .name("new test name")
                .email("test@mail.ru")
                .birthday(LocalDate.of(1933, 3, 25))

                .build();

        userDao.updateUser(changedUser);

        assertNotEquals(createdUser, userDao.getUserById(1L));
        assertEquals(changedUser, userDao.getUserById(1L));
    }

    @Test
    public void updateUser_shouldThrowNotFoundExceptionWhenIdDoesNotExist() {
        User changedUser = User.builder()
                .id(1L)
                .login("login")
                .name("new test name")
                .email("test@mail.ru")
                .birthday(LocalDate.of(1933, 3, 25))
                .build();

        assertThrows(UserNotFoundException.class, () -> userDao.updateUser(changedUser));
    }

    @Test
    public void updateUser_shouldThrowNotFoundExceptionWhenIdIsNotMentioned() {
        User changedUser = User.builder()
                .login("login")
                .name("new test name")
                .email("test@mail.ru")
                .birthday(LocalDate.of(1933, 3, 25))
                .build();

        assertThrows(UserNotFoundException.class, () -> userDao.updateUser(changedUser));
    }

    /*
    List<User> getUsers(); 2/2
    User getUserById(Long id); 2/2
    User createUser(User user); 2/2
    boolean deleteUserById(Long id); 2/2
    User updateUser(User user); 3/3
    */
}