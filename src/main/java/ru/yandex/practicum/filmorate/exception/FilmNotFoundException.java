package ru.yandex.practicum.filmorate.exception;

public class FilmNotFoundException extends RuntimeException {
    public FilmNotFoundException() {
        super();
    }

    public FilmNotFoundException(final String message) {
        super(message);
    }
}
