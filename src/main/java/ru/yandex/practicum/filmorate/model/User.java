package ru.yandex.practicum.filmorate.model;


import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Data;
import ru.yandex.practicum.filmorate.validation.ContainsNoSpaces;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.PastOrPresent;
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
    @NotNull
    @PastOrPresent
    private LocalDate birthday;
    @JsonIgnore
    private Set<Integer> friendsList = new HashSet<>();

    public User(int id, String email, String login, LocalDate birthday) {
        this.id = id;
        this.email = email;
        this.login = login;
        this.name = login;
        this.birthday = birthday;
    }
}
