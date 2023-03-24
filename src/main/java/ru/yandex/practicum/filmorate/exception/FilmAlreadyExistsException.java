package ru.yandex.practicum.filmorate.exception;

public class FilmAlreadyExistsException extends RuntimeException {
    public FilmAlreadyExistsException() {
        super();
    }

    public FilmAlreadyExistsException(final String message) {
        super(message);
    }
}
