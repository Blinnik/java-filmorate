package ru.yandex.practicum.filmorate.exception;

public class UserAlreadyExistsException extends RuntimeException {
    public UserAlreadyExistsException(final String message) {
        super(message);
    }
}
