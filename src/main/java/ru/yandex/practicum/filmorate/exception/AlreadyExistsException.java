package ru.yandex.practicum.filmorate.exception;

public class AlreadyExistsException extends RuntimeException {
    public AlreadyExistsException(final String message) {
        super(message);
    }
}
