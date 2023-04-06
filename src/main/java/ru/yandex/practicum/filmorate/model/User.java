package ru.yandex.practicum.filmorate.model;


import javax.validation.constraints.*;
import lombok.Builder;
import lombok.Data;

import java.time.LocalDate;
import java.util.HashSet;
import java.util.Objects;
import java.util.Set;

@Data
@Builder(toBuilder = true)
public class User {
    private Long id;
    @Email
    @NotEmpty
    private String email;
    @NotEmpty
    @Pattern(regexp = "^\\S*$")
    private String login;
    private String name;

    @PastOrPresent
    private LocalDate birthday;

    final private Set<Long> friends = new HashSet<>();


    public void addFriend(Long friendId) {
        friends.add(friendId);
    }

    public void deleteFriend(Long friendId) {
        friends.remove(friendId);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        User user = (User) o;
        return Objects.equals(id, user.id) && Objects.equals(email, user.email) && Objects.equals(login, user.login) && Objects.equals(name, user.name) && Objects.equals(birthday, user.birthday) && Objects.equals(friends, user.friends);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, email, login, name, birthday, friends);
    }
}
