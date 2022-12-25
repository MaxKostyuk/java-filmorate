package ru.yandex.practicum.filmorate.model;


import lombok.Data;
import ru.yandex.practicum.filmorate.validation.ContainsNoSpaces;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Past;
import java.time.LocalDate;
import java.util.HashSet;
import java.util.Set;

@Data
public class User {

    private int id;
    @NotBlank
    @Email
    private String email;
    @NotBlank
    @ContainsNoSpaces
    private String login;
    private String name;
    @Past
    private LocalDate birthday;
    @NotNull
    private Set<Integer> friendsList;

    public User(int id, String email, String login, LocalDate birthday, Set<Integer> friendsList) {
        this.id = id;
        this.email = email;
        this.login = login;
        this.name = login;
        this.birthday = birthday;
        this.friendsList = friendsList;
    }
}
