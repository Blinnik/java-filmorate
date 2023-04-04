package ru.yandex.practicum.filmorate.model;


import javax.validation.constraints.*;
import lombok.Builder;
import lombok.Data;

import java.time.LocalDate;

@Data
@Builder(toBuilder = true)
public class User {
    private Integer id;
    @Email
    @NotEmpty
    private String email;
    @NotEmpty
    @Pattern(regexp = "^\\S*$")
    private String login;
    private String name;

    @PastOrPresent
    private LocalDate birthday;
}
