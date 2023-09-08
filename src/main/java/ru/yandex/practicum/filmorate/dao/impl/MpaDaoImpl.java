package ru.yandex.practicum.filmorate.dao.impl;

import lombok.AccessLevel;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.dao.MpaDao;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.model.Mpa;

import java.util.List;

@Component
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@Slf4j
public class MpaDaoImpl implements MpaDao {
    JdbcTemplate jdbcTemplate;

    RowMapper<Mpa> mpaRowMapper = (rs, rowNum) -> Mpa.builder()
            .id(rs.getShort("mpa_id"))
            .name(rs.getString("name"))
            .build();

    @Autowired
    public MpaDaoImpl(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    @Override
    public Mpa getMpaById(Short id) {
        try {
            String sql = "select * from mpas where mpa_id = ?";
            return jdbcTemplate.queryForObject(sql, mpaRowMapper, id);
        } catch (EmptyResultDataAccessException e) {
            log.warn("MPA с ID={} не существует", id);
            throw new NotFoundException("MPA с ID=" + id + " не существует");
        }
    }

    @Override
    public List<Mpa> getMpas() {
        String sql = "select * from mpas";
        return jdbcTemplate.query(sql, mpaRowMapper);
    }

    @Override
    public String getMpaName(Short mpaId) {
        return jdbcTemplate.queryForObject(
                "select name from mpas where mpa_id = ?",
                String.class,
                mpaId
        );
    }
}
