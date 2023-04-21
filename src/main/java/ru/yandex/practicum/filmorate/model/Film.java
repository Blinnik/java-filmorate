package ru.yandex.practicum.filmorate.model;

import lombok.AccessLevel;
import lombok.Builder;
import lombok.Data;
import lombok.experimental.FieldDefaults;
import ru.yandex.practicum.filmorate.annotation.FilmReleaseDateConstraint;

import javax.validation.constraints.*;
import java.time.LocalDate;
import java.util.HashSet;
import java.util.Set;

@Data
@Builder(toBuilder = true)
@FieldDefaults(level = AccessLevel.PRIVATE)
public class Film {
    Long id;
    @NotEmpty
    String name;
    @Size(max = 200, message = "Описание не должно превышать 200 символов")
    String description;
    @NotNull
    @FilmReleaseDateConstraint
    LocalDate releaseDate;
    @Positive
    Integer duration;
    final Set<Long> likes = new HashSet<>();

    public void addLike(Long id) {
        likes.add(id);
    }

    public void removeLike(Long id) {
        likes.remove(id);
    }
}
