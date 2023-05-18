package ru.yandex.practicum.filmorate.dao;

import ru.yandex.practicum.filmorate.model.Mpa;

import java.util.List;

public interface MpaDao {
    Mpa getMpaById(Short id);

    List<Mpa> getMpas();

    String getMpaName(Short mpaId);
}
