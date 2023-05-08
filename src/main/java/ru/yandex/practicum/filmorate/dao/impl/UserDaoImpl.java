package ru.yandex.practicum.filmorate.dao.impl;

import lombok.AccessLevel;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.simple.SimpleJdbcInsert;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.dao.UserDao;
import ru.yandex.practicum.filmorate.exception.UserNotFoundException;
import ru.yandex.practicum.filmorate.model.User;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.List;

@Component
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@Slf4j
public class UserDaoImpl implements UserDao {
    JdbcTemplate jdbcTemplate;

    @Autowired
    public UserDaoImpl(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    @Override
    public List<User> getUsers() {
        String sql = "select * from users";
        return jdbcTemplate.query(sql, (rs, rowNum) -> mapRowToUser(rs));
    }

    @Override
    public User getUserById(Long id) {
        String sql = "select * from users where user_id = ?";

        try {
            return jdbcTemplate.queryForObject(sql, (rs, rowNum) -> mapRowToUser(rs), id);
        } catch (EmptyResultDataAccessException e) {
            log.warn("Пользователь с ID={} не найден", id);
            throw new UserNotFoundException("Пользователя с указанным ID не существует");
        }
    }

    @Override
    public User createUser(User user) {
        SimpleJdbcInsert simpleJdbcInsert = new SimpleJdbcInsert(jdbcTemplate)
                .withTableName("users")
                .usingGeneratedKeyColumns("user_id");

        setNameIfEmpty(user);
        Long userId = simpleJdbcInsert.executeAndReturnKey(user.toMap()).longValue();

        user.setId(userId);

        log.info("Был создан новый пользователь {}", user);
        return user;
    }

    @Override
    public boolean deleteUserById(Long id) {
        String sql = "delete from users where user_id = ?";
        if (jdbcTemplate.update(sql, id) > 0) {
            log.info("Пользователь с ID={} был успешно удален", id);
            return true;
        }
        log.warn("Не удалось удалить пользователя с ID={}", id);
        return false;
    }

    @Override
    public User updateUser(User user) {
        String sql = "update users " +
                "set email = ?," +
                "login = ?," +
                "name = ?," +
                "birthday = ? " +
                "where user_id = ?";

        setNameIfEmpty(user);

        Long userId = user.getId();

        if (jdbcTemplate.update(sql, user.getEmail(), user.getLogin(), user.getName(),
                user.getBirthday(), userId) > 0) {
            log.info("Пользователь с ID={} обновлен", userId);
            return user;
        }

        log.warn("Пользователь с ID={} не найден", userId);
        throw new UserNotFoundException("Пользователя с указанным ID не существует");
    }

    private void setNameIfEmpty(User user) {
        if (user.getName() == null || user.getName().equals("")) {
            user.setName(user.getLogin());
        }
    }

    private User mapRowToUser(ResultSet rs) throws SQLException {
        Long id = rs.getLong("user_id");
        String email = rs.getString("email");
        String login = rs.getString("login");
        String name = rs.getString("name");
        LocalDate birthday = rs.getDate("birthday").toLocalDate();

        return User.builder()
                .id(id)
                .email(email)
                .login(login)
                .name(name)
                .birthday(birthday)
                .build();
    }
}
