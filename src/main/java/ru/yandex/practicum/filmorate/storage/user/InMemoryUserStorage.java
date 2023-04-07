package ru.yandex.practicum.filmorate.storage.user;

import lombok.AccessLevel;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.exception.UserAlreadyExistsException;
import ru.yandex.practicum.filmorate.exception.UserNotFoundException;
import ru.yandex.practicum.filmorate.model.User;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

@Component
@Slf4j
@FieldDefaults(level = AccessLevel.PRIVATE)
public class InMemoryUserStorage implements UserStorage {
    final Map<Long, User> usersById = new HashMap<>();
    static long lastId = 1;

    @Override
    public Collection<User> getUsers() {
        return usersById.values();
    }

    @Override
    public User createUser(User user) {
        if (usersById.containsValue(user)) {
            log.warn("Пользователь {} уже существует", user);
            throw new UserAlreadyExistsException("Пользователь уже существует");
        }
        setNameIfNull(user);

        user.setId(lastId++);
        usersById.put(user.getId(), user);
        log.info("Пользователь был добавлен: {}", user);
        return user;
    }

    @Override
    public void deleteUserById(Long id) {
        if (!usersById.containsKey(id)) {
            log.warn("userId={} не существует", id);
            throw new UserNotFoundException("Пользователя с указанным Id не существует");
        }
        usersById.remove(id);
        log.info("Пользователь ID#{} был удален", id);
    }

    @Override
    public User updateUser(User user) {
        setNameIfNull(user);
        Long userId = user.getId();
        if (!usersById.containsKey(userId)) {
            log.warn("userId={} не существует", userId);
            throw new UserNotFoundException("Пользователя с указанным Id не существует");
        }
        usersById.put(userId, user);
        log.info("Пользователь был обновлен: {}", user);
        return user;
    }

    public User getUserById(Long id) {
        User user = usersById.get(id);
        if (user == null) {
            throw new UserNotFoundException("Пользователя с id " + id + " не существует");
        }
        return user;
    }

    private void setNameIfNull(User user) {
        if (user.getName() == null || user.getName().equals("")) {
            user.setName(user.getLogin());
        }
    }
}
