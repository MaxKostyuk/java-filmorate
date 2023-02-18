package ru.yandex.practicum.filmorate.model;

import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Positive;

@Data
public class Review {

    private int reviewId;
    @NotBlank
    private String content;
    private boolean isPositive;
    @Positive
    private int userId;
    @Positive
    private int filmId;
    private int useful;

    public boolean getIsPositive() {
        return isPositive;
    }

    public void setIsPositive(boolean positive) {
        isPositive = positive;
    }
}
