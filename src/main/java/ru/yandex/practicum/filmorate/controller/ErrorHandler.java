package ru.yandex.practicum.filmorate.controller;

import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;
import ru.yandex.practicum.filmorate.exception.*;

@RestControllerAdvice
public class ErrorHandler {

    @ExceptionHandler({FilmNotFoundException.class, UserNotFoundException.class,
            MpaNotFoundException.class, GenreNotFoundException.class})
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public ErrorResponse handleNotFoundExceptions(final RuntimeException e) {
        return new ErrorResponse(e.getClass().getSimpleName(), e.getMessage(), HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler({FilmAlreadyExistsException.class, UserAlreadyExistsException.class})
    @ResponseStatus(HttpStatus.CONFLICT)
    public ErrorResponse handleAlreadyExistExceptions(final RuntimeException e) {
        return new ErrorResponse(e.getClass().getSimpleName(), e.getMessage(), HttpStatus.CONFLICT);
    }

    @ExceptionHandler
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ErrorResponse handleMethodArgumentTypeMismatchException(final MethodArgumentTypeMismatchException e) {
        return new ErrorResponse(e.getClass().getSimpleName(), e.getMessage(), HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public ErrorResponse handleThrowable(final Throwable e) {
        return new ErrorResponse(e.getClass().getSimpleName(), e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
    }

    @AllArgsConstructor
    static class ErrorResponse {
        String error;
        String errorMessage;
        HttpStatus responseCodeStatus;

        public String getError() {
            return error;
        }

        public String getErrorMessage() {
            return errorMessage;
        }

        public HttpStatus getResponseCodeStatus() {
            return responseCodeStatus;
        }
    }
}