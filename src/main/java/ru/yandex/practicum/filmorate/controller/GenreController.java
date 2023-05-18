package ru.yandex.practicum.filmorate.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import ru.yandex.practicum.filmorate.dao.GenreDao;
import ru.yandex.practicum.filmorate.model.Genre;

import java.util.List;

@RestController
@RequestMapping("/genres")
public class GenreController {
    GenreDao genreDao;

    @Autowired
    public GenreController(GenreDao genreDao) {
        this.genreDao = genreDao;
    }

    @GetMapping
    public List<Genre> getGenres() {
        return genreDao.getGenres();
    }

    @GetMapping("/{id}")
    public Genre getGenreById(@PathVariable Short id) {
        return genreDao.getGenreById(id);
    }
}
