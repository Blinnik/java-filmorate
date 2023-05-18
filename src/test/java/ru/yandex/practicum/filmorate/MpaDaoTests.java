package ru.yandex.practicum.filmorate;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.dao.EmptyResultDataAccessException;
import ru.yandex.practicum.filmorate.dao.MpaDao;
import ru.yandex.practicum.filmorate.exception.MpaNotFoundException;
import ru.yandex.practicum.filmorate.model.Mpa;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.assertEquals;

@SpringBootTest
@AutoConfigureTestDatabase
@RequiredArgsConstructor(onConstructor_ = @Autowired)
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class MpaDaoTests {
    MpaDao mpaDao;

    @Test
    public void getMpaById_shouldThrowMpaNotFoundException() {
        assertThrows(MpaNotFoundException.class, () -> mpaDao.getMpaById((short) 999));
    }

    @Test
    public void getMpaById_shouldReturnMpaCorrectly() {
        assertEquals(Mpa.builder().id((short) 1).name("G").build(), mpaDao.getMpaById((short) 1));
        assertEquals(Mpa.builder().id((short) 2).name("PG").build(), mpaDao.getMpaById((short) 2));
        assertEquals(Mpa.builder().id((short) 3).name("PG-13").build(), mpaDao.getMpaById((short) 3));
        assertEquals(Mpa.builder().id((short) 4).name("R").build(), mpaDao.getMpaById((short) 4));
        assertEquals(Mpa.builder().id((short) 5).name("NC-17").build(), mpaDao.getMpaById((short) 5));
    }

    @Test
    public void getMpas_shouldReturnAllMpas() {
        assertEquals(List.of(
                Mpa.builder().id((short) 1).name("G").build(),
                Mpa.builder().id((short) 2).name("PG").build(),
                Mpa.builder().id((short) 3).name("PG-13").build(),
                Mpa.builder().id((short) 4).name("R").build(),
                Mpa.builder().id((short) 5).name("NC-17").build()
        ), mpaDao.getMpas());
    }

    @Test
    public void getMpaName_shouldThrowExceptionIfIdDoesNotExist() {
        assertThrows(EmptyResultDataAccessException.class, () -> mpaDao.getMpaName((short) 999));
    }

    @Test
    public void getMpaName_shouldGetNameOfMpa() {
        assertEquals("G", mpaDao.getMpaName((short) 1));
    }

    /*
    Mpa getMpaById(Short id); 2/2
    List<Mpa> getMpas(); 1/1
    String getMpaName(Short mpaId); 2/2
    */
}
