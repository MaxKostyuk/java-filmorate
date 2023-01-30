package ru.yandex.practicum.filmorate.model;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Positive;

public class Rating {
    @Positive
    int ratingId;
    @NotBlank
    String ratingName;
}
