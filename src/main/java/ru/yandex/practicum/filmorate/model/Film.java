package ru.yandex.practicum.filmorate.model;


import lombok.Data;
import ru.yandex.practicum.filmorate.validation.PositiveDuration;
import ru.yandex.practicum.filmorate.validation.ReleaseDateValidation;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Positive;
import javax.validation.constraints.Size;
import java.time.Duration;
import java.time.LocalDate;

@Data
public class Film {

    private int id;
    @NotBlank(message = "Name must not be blank")
    private String name;
    @Size(max = 200, message = "Description must be above 200 characters")
    private String description;
    @ReleaseDateValidation
    private LocalDate releaseDate;
    @Positive
    private int duration;

}
