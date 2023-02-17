package ru.yandex.practicum.filmorate.model;

import lombok.RequiredArgsConstructor;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Positive;

@RequiredArgsConstructor
public class Review {

    @Positive
    private int reviewId;
    @NotBlank
    private String content;
    private boolean isPositive;
    @Positive
    private int userId;
    @Positive
    private int filmId;
    private int useful;
}
