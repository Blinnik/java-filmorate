package ru.yandex.practicum.filmorate.model;


import javax.validation.constraints.*;

import lombok.AccessLevel;
import lombok.Builder;
import lombok.Data;
import lombok.experimental.FieldDefaults;

import java.time.LocalDate;
import java.util.*;

@Data
@Builder(toBuilder = true)
@FieldDefaults(level = AccessLevel.PRIVATE)
public class User {
    Long id;
    @Email
    @NotEmpty
    String email;
    @NotEmpty
    @Pattern(regexp = "^\\S*$")
    String login;
    String name;
    @PastOrPresent
    LocalDate birthday;

    public Map<String, Object> toMap() {
        Map<String, Object> values = new HashMap<>();
        values.put("email", email);
        values.put("login", login);
        values.put("name", name);
        values.put("birthday", birthday);

        return values;
    }
}
