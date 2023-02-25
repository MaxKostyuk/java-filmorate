package ru.yandex.practicum.filmorate.model;


import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Data;
import lombok.NoArgsConstructor;
import ru.yandex.practicum.filmorate.validation.ReleaseDateValidation;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Positive;
import javax.validation.constraints.Size;
import java.time.LocalDate;
import java.util.HashSet;
import java.util.Set;

@Data
@NoArgsConstructor
public class Film {

    private int id;
    @NotBlank(message = "Name must be not blank")
    private String name;
    @Size(max = 200, message = "Description must be above 200 characters")
    private String description;
    @ReleaseDateValidation
    private LocalDate releaseDate;
    @Positive
    private int duration;
    @JsonIgnore
    private Set<Integer> likesFromUsers;
    @NotNull
    private Rating mpa;
    private Set<Genre> genres;

    public Film(int id, String name, String description, LocalDate releaseDate, int duration, Rating mpa, Set<Genre> genres) {
        this.id = id;
        this.name = name;
        this.description = description;
        this.releaseDate = releaseDate;
        this.duration = duration;
        this.likesFromUsers = new HashSet<>();
        this.mpa = mpa;
        if (genres != null)
            this.genres = genres;
        else
            this.genres = new HashSet<>();
    }
}
