package ru.yandex.practicum.filmorate;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
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

    int correctId = 1;
    String nullName = null;
    String blankName = "";
    String nameOnlySpaces = "  ";
    String correctName = "name";
    String descriptionMore200Characters = "123456789012345678901234567890123456789012345678901234567890123456789012345678901234567890123456789012345678901234567890123456789012345678901234567890123456789012345678901234567890123456789012345678901";
    String correctDescription = "correct description";
    LocalDate incorrectReleaseDate = LocalDate.of(1894, Month.DECEMBER, 1);
    LocalDate correctReleaseDate = LocalDate.now();
    Rating correctMpa = new Rating();
    Set<Genre> correctGenre = new HashSet<>();
    int incorrectDuration = 0;
    int correctDuration = 1;
    Validator validator = Validation.buildDefaultValidatorFactory().getValidator();

    @Test
    public void filmValidation_whenNullName_returnConstraintViolation() {
        Film film = new Film(correctId, nullName, correctDescription, correctReleaseDate, correctDuration, correctMpa, correctGenre);

        Set<ConstraintViolation<Film>> violations = validator.validate(film);
        ConstraintViolation<Film> violation = violations.stream().findFirst()
                .orElseThrow(() -> new RuntimeException("Отсутствует ошибка валидации"));

        Assertions.assertEquals(1, violations.size());
        Assertions.assertEquals("name", violation.getPropertyPath().toString());
    }

    @Test
    public void filmValidation_whenBlankName_returnConstraintViolation() {
        Film film = new Film(correctId, blankName, correctDescription, correctReleaseDate, correctDuration, correctMpa, correctGenre);

        Set<ConstraintViolation<Film>> violations = validator.validate(film);
        ConstraintViolation<Film> violation = violations.stream().findFirst()
                .orElseThrow(() -> new RuntimeException("Отсутствует ошибка валидации"));

        Assertions.assertEquals(1, violations.size());
        Assertions.assertEquals("name", violation.getPropertyPath().toString());
    }

    @Test
    public void filmValidation_whenNameOnlySpaces_returnConstraintViolation() {
        Film film = new Film(correctId, nameOnlySpaces, correctDescription, correctReleaseDate, correctDuration, correctMpa, correctGenre);

        Set<ConstraintViolation<Film>> violations = validator.validate(film);
        ConstraintViolation<Film> violation = violations.stream().findFirst()
                .orElseThrow(() -> new RuntimeException("Отсутствует ошибка валидации"));

        Assertions.assertEquals(1, violations.size());
        Assertions.assertEquals("name", violation.getPropertyPath().toString());
    }

    @Test
    public void filmValidation_whenDescriptionMoreThan200Characters_returnConstraintViolation() {
        Film film = new Film(correctId, correctName, descriptionMore200Characters, correctReleaseDate, correctDuration, correctMpa, correctGenre);

        Set<ConstraintViolation<Film>> violations = validator.validate(film);
        ConstraintViolation<Film> violation = violations.stream().findFirst()
                .orElseThrow(() -> new RuntimeException("Отсутствует ошибка валидации"));

        Assertions.assertEquals(1, violations.size());
        Assertions.assertEquals("description", violation.getPropertyPath().toString());
    }

    @Test
    public void filmValidation_whenIncorrectReleaseDate_returnConstraintViolation() {
        Film film = new Film(correctId, correctName, correctDescription, incorrectReleaseDate, correctDuration, correctMpa, correctGenre);

        Set<ConstraintViolation<Film>> violations = validator.validate(film);
        ConstraintViolation<Film> violation = violations.stream().findFirst()
                .orElseThrow(() -> new RuntimeException("Отсутствует ошибка валидации"));

        Assertions.assertEquals(1, violations.size());
        Assertions.assertEquals("releaseDate", violation.getPropertyPath().toString());
    }

    @Test
    public void filmValidation_whenIncorrectDuration_returnConstraintViolation() {
        Film film = new Film(correctId, correctName, correctDescription, correctReleaseDate, incorrectDuration, correctMpa, correctGenre);

        Set<ConstraintViolation<Film>> violations = validator.validate(film);
        ConstraintViolation<Film> violation = violations.stream().findFirst()
                .orElseThrow(() -> new RuntimeException("Отсутствует ошибка валидации"));

        Assertions.assertEquals(1, violations.size());
        Assertions.assertEquals("duration", violation.getPropertyPath().toString());
    }

    @Test
    public void filmValidation_whenAllFieldsCorrect_returnNoConstraintViolation() {
        Film film = new Film(correctId, correctName, correctDescription, correctReleaseDate, correctDuration, correctMpa, correctGenre);

        Set<ConstraintViolation<Film>> violations = validator.validate(film);

        Assertions.assertEquals(0, violations.size());
    }
}
