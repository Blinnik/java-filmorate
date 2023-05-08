package ru.yandex.practicum.filmorate.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.dao.FilmDao;
import ru.yandex.practicum.filmorate.dao.UserDao;

import java.util.List;

@Service
@Slf4j
public class FilmService {
    FilmDao filmDao;
    UserDao userDao;

    @Autowired
    public FilmService(FilmDao filmDao, UserDao userDao) {
        this.filmDao = filmDao;
        this.userDao = userDao;
    }

    public void addLike(Long filmId, Long userId) {
        filmDao.getFilmById(filmId);
        userDao.getUserById(userId);
        filmDao.addLike(filmId, userId);
        log.info("Лайк фильму с ID={} был добавлен пользователем с ID={}", filmId, userId);
    }

    public void removeLike(Long filmId, Long userId) {
        filmDao.getFilmById(filmId);
        userDao.getUserById(userId);
        filmDao.removeLike(filmId, userId);
        log.info("Лайк фильму с ID={} был убран пользователем с ID={}", filmId, userId);
    }

    public List<Film> getPopularFilms(Integer count) {
        return filmDao.getPopularFilms(count);
    }
}
