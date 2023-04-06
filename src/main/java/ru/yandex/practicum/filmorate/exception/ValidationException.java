package ru.yandex.practicum.filmorate.exception;

// Пока не используется, поскольку в тестах не нужно проверять параметры
// и, например, id = -1 должен выводить UserNotFoundException, а не
// ValidationException
public class ValidationException extends RuntimeException {
    public ValidationException(final String message) {
        super(message);
    }
}
