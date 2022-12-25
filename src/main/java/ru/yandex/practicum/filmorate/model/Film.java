package ru.yandex.practicum.filmorate.model;


import lombok.AllArgsConstructor;
import lombok.Data;
import ru.yandex.practicum.filmorate.validation.ReleaseDateValidation;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Positive;
import javax.validation.constraints.Size;
import java.time.LocalDate;
import java.util.Set;

@Data
@AllArgsConstructor
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
    @NotNull
    private Set<Integer> likesFromUsers;

}
