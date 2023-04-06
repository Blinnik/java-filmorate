package ru.yandex.practicum.filmorate.model;

import lombok.Builder;
import lombok.Data;
import ru.yandex.practicum.filmorate.annotation.FilmReleaseDateConstraint;

import javax.validation.constraints.*;
import java.time.LocalDate;
import java.util.*;

@Data
@Builder(toBuilder = true)
public class Film {
    private Long id;
    @NotEmpty
    private String name;
    @Size(max = 200, message = "Описание не должно превышать 200 символов")
    private String description;
    @NotNull
    @FilmReleaseDateConstraint
    private LocalDate releaseDate;
    @Positive
    private Integer duration;
    final Set<Long> likes = new HashSet<>();

    public void addLike(Long id) {
        likes.add(id);
    }

    public void removeLike(Long id) {
        likes.remove(id);
    }
}
