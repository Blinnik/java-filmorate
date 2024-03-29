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
import ru.yandex.practicum.filmorate.dao.FriendshipDao;
import ru.yandex.practicum.filmorate.dao.UserDao;
import ru.yandex.practicum.filmorate.model.User;

import java.time.LocalDate;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.assertFalse;

@SpringBootTest
@AutoConfigureTestDatabase
@RequiredArgsConstructor(onConstructor_ = @Autowired)
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class FriendshipDaoTests {
    UserDao userDao;
    FriendshipDao friendshipDao;
    JdbcTemplate jdbcTemplate;

    @AfterEach
    public void beforeEach() {
        JdbcTestUtils.deleteFromTables(jdbcTemplate,"users");
        jdbcTemplate.update("alter table users alter column user_id restart with 1");
    }

    @Test
    public void getFriendsIds_shouldReturnEmptyList() {
        assertEquals(List.of(), friendshipDao.getFriendsIds(1L));
    }

    @Test
    public void addFriendship_shouldNotAddFriendIfIdDoesNotExist() {
        assertThrows(DataIntegrityViolationException.class, () -> friendshipDao.addFriendship(1L, 2L));
    }

    @Test
    public void addFriendship_shouldNotAddSelfAsAFriend() {
        User user = User.builder()
                .login("dolore")
                .name("check")
                .email("mail@mail.ru")
                .birthday(LocalDate.of(1946, 8, 20))
                .build();

        userDao.createUser(user);

        assertFalse(friendshipDao.addFriendship(1L, 1L));
    }

    @Test
    public void addFriendship_shouldAddFriendInOneDirection() {
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

        userDao.createUser(user1);
        userDao.createUser(user2);
        // friendship is one-sided, a friend is displayed only for a user with id=2
        friendshipDao.addFriendship(1L, 2L);

        assertEquals(List.of(), friendshipDao.getFriendsIds(1L));
        assertEquals(List.of(1L), friendshipDao.getFriendsIds(2L));
    }

    @Test
    public void addFriendship_shouldAddFriendInBothDirections() {
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

        userDao.createUser(user1);
        userDao.createUser(user2);

        friendshipDao.addFriendship(1L, 2L);
        friendshipDao.addFriendship(2L, 1L);

        assertEquals(List.of(2L), friendshipDao.getFriendsIds(1L));
        assertEquals(List.of(1L), friendshipDao.getFriendsIds(2L));
    }

    @Test
    public void getFriends_shouldReturnFriendsCorrectly() {
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

        User user3 = User.builder()
                .login("alex")
                .name("Vova Vist")
                .email("boblin@mail.ru")
                .birthday(LocalDate.of(1946, 8, 20))
                .build();

        userDao.createUser(user1);
        userDao.createUser(user2);
        userDao.createUser(user3);

        friendshipDao.addFriendship(1L, 3L);
        friendshipDao.addFriendship(2L, 3L);
        friendshipDao.addFriendship(3L, 2L);

        assertEquals(List.of(), friendshipDao.getFriendsIds(1L));
        assertEquals(List.of(3L), friendshipDao.getFriendsIds(2L));
        assertEquals(List.of(1L, 2L), friendshipDao.getFriendsIds(3L));
    }

    @Test
    public void updateFriendship_shouldReturnFalseIfIdAreEqual() {
        assertFalse(friendshipDao.updateFriendship(1L, 1L, true));
    }

    @Test
    public void updateFriendship_shouldReturnFalseIfIdDoesNotExist() {
        assertFalse(friendshipDao.updateFriendship(1L, 2L, true));
    }

    @Test
    public void updateFriendship_shouldUpdateFriendship() {
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

        userDao.createUser(user1);
        userDao.createUser(user2);

        friendshipDao.addFriendship(1L, 2L);
        assertEquals(List.of(), friendshipDao.getFriendsIds(1L));
        assertEquals(List.of(1L), friendshipDao.getFriendsIds(2L));
        assertTrue(friendshipDao.updateFriendship(1L, 2L, true));
        // now the first user has a second friend
        assertEquals(List.of(2L), friendshipDao.getFriendsIds(1L));
        assertEquals(List.of(1L), friendshipDao.getFriendsIds(2L));
    }

    @Test
    public void deleteFriendship_shouldDeleteFriendship() {
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

        userDao.createUser(user1);
        userDao.createUser(user2);

        friendshipDao.addFriendship(1L, 2L);
        assertEquals(List.of(1L), friendshipDao.getFriendsIds(2L));
        assertTrue(friendshipDao.deleteFriendship(1L, 2L));
        assertEquals(List.of(), friendshipDao.getFriendsIds(2L));
    }

    @Test
    public void deleteFriendship_shouldNotDeleteFriendshipIfIdDoesNotExist() {
        assertFalse(friendshipDao.deleteFriendship(1L, 2L));
    }
}
