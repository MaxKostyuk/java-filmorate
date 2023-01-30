package ru.yandex.practicum.filmorate.model;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Positive;

public class Genre {
    @Positive
    int genreId;
    @NotBlank
    String genreName;
}
