package ru.yandex.practicum.filmorate.model;

import lombok.AccessLevel;
import lombok.Builder;
import lombok.Data;
import lombok.experimental.FieldDefaults;
import ru.yandex.practicum.filmorate.annotation.FilmReleaseDateConstraint;

import javax.validation.constraints.*;
import java.time.LocalDate;
import java.util.*;

@Data
@Builder(toBuilder = true)
@FieldDefaults(level = AccessLevel.PRIVATE)
public class Film {
    Long id;
    @NotEmpty
    @Size(max = 64, message = "Название фильма не должно превышать 64 символа")
    String name;
    @Size(max = 200, message = "Описание не должно превышать 200 символов")
    String description;
    @NotNull
    @FilmReleaseDateConstraint
    LocalDate releaseDate;
    @Positive
    Short duration;
    Mpa mpa;
    Set<Long> likes;
    Set<Genre> genres;

    public Map<String, Object> toMap() {
        Map<String, Object> values = new HashMap<>();
        values.put("name", name);
        values.put("description", description);
        values.put("release_date", releaseDate);
        values.put("duration", duration);
        values.put("mpa_id", mpa.getId());

        return values;
    }
}
