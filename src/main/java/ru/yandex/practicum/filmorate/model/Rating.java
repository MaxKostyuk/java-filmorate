package ru.yandex.practicum.filmorate.model;

import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Positive;

@Data
public class Rating {
    @Positive
    int id;
    @NotBlank
    String name;
}
