package ru.yandex.practicum.filmorate;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import ru.yandex.practicum.filmorate.model.Director;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.model.Rating;

import javax.validation.ConstraintViolation;
import javax.validation.Validation;
import javax.validation.Validator;
import java.time.LocalDate;
import java.time.Month;
import java.util.HashSet;
import java.util.Set;

public class FilmValidationTest {

    private int correctId = 1;
    private String nullName = null;
    private String blankName = "";
    private String nameOnlySpaces = "  ";
    private String correctName = "name";
    private String descriptionMore200Characters = "123456789012345678901234567890123456789012345678901234567890123456789012345678901234567890123456789012345678901234567890123456789012345678901234567890123456789012345678901234567890123456789012345678901";
    private String correctDescription = "correct description";
    private LocalDate incorrectReleaseDate = LocalDate.of(1894, Month.DECEMBER, 1);
    private LocalDate correctReleaseDate = LocalDate.now();
    private Rating correctMpa = new Rating();
    private Set<Genre> correctGenre = new HashSet<>();
    private Set<Director> directors = new HashSet<>();
    private int incorrectDuration = 0;
    private int correctDuration = 1;
    private Validator validator = Validation.buildDefaultValidatorFactory().getValidator();

    @Test
    public void filmValidation_whenNullName_returnConstraintViolation() {
        Film film = new Film(correctId, nullName, correctDescription, correctReleaseDate, correctDuration, correctMpa, correctGenre, directors);

        Set<ConstraintViolation<Film>> violations = validator.validate(film);
        ConstraintViolation<Film> violation = violations.stream().findFirst()
                .orElseThrow(() -> new RuntimeException("Отсутствует ошибка валидации"));

        Assertions.assertEquals(1, violations.size());
        Assertions.assertEquals("name", violation.getPropertyPath().toString());
    }

    @Test
    public void filmValidation_whenBlankName_returnConstraintViolation() {
        Film film = new Film(correctId, blankName, correctDescription, correctReleaseDate, correctDuration, correctMpa, correctGenre, directors);

        Set<ConstraintViolation<Film>> violations = validator.validate(film);
        ConstraintViolation<Film> violation = violations.stream().findFirst()
                .orElseThrow(() -> new RuntimeException("Отсутствует ошибка валидации"));

        Assertions.assertEquals(1, violations.size());
        Assertions.assertEquals("name", violation.getPropertyPath().toString());
    }

    @Test
    public void filmValidation_whenNameOnlySpaces_returnConstraintViolation() {
        Film film = new Film(correctId, nameOnlySpaces, correctDescription, correctReleaseDate, correctDuration, correctMpa, correctGenre, directors);

        Set<ConstraintViolation<Film>> violations = validator.validate(film);
        ConstraintViolation<Film> violation = violations.stream().findFirst()
                .orElseThrow(() -> new RuntimeException("Отсутствует ошибка валидации"));

        Assertions.assertEquals(1, violations.size());
        Assertions.assertEquals("name", violation.getPropertyPath().toString());
    }

    @Test
    public void filmValidation_whenDescriptionMoreThan200Characters_returnConstraintViolation() {
        Film film = new Film(correctId, correctName, descriptionMore200Characters, correctReleaseDate, correctDuration, correctMpa, correctGenre, directors);

        Set<ConstraintViolation<Film>> violations = validator.validate(film);
        ConstraintViolation<Film> violation = violations.stream().findFirst()
                .orElseThrow(() -> new RuntimeException("Отсутствует ошибка валидации"));

        Assertions.assertEquals(1, violations.size());
        Assertions.assertEquals("description", violation.getPropertyPath().toString());
    }

    @Test
    public void filmValidation_whenIncorrectReleaseDate_returnConstraintViolation() {
        Film film = new Film(correctId, correctName, correctDescription, incorrectReleaseDate, correctDuration, correctMpa, correctGenre, directors);

        Set<ConstraintViolation<Film>> violations = validator.validate(film);
        ConstraintViolation<Film> violation = violations.stream().findFirst()
                .orElseThrow(() -> new RuntimeException("Отсутствует ошибка валидации"));

        Assertions.assertEquals(1, violations.size());
        Assertions.assertEquals("releaseDate", violation.getPropertyPath().toString());
    }

    @Test
    public void filmValidation_whenIncorrectDuration_returnConstraintViolation() {
        Film film = new Film(correctId, correctName, correctDescription, correctReleaseDate, incorrectDuration, correctMpa, correctGenre, directors);

        Set<ConstraintViolation<Film>> violations = validator.validate(film);
        ConstraintViolation<Film> violation = violations.stream().findFirst()
                .orElseThrow(() -> new RuntimeException("Отсутствует ошибка валидации"));

        Assertions.assertEquals(1, violations.size());
        Assertions.assertEquals("duration", violation.getPropertyPath().toString());
    }

    @Test
    public void filmValidation_whenAllFieldsCorrect_returnNoConstraintViolation() {
        Film film = new Film(correctId, correctName, correctDescription, correctReleaseDate, correctDuration, correctMpa, correctGenre, directors);

        Set<ConstraintViolation<Film>> violations = validator.validate(film);

        Assertions.assertEquals(0, violations.size());
    }
}
