package ru.yandex.practicum.filmorate;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import ru.yandex.practicum.filmorate.model.Film;

import javax.validation.ConstraintViolation;
import javax.validation.Validation;
import javax.validation.Validator;
import java.time.LocalDate;
import java.time.Month;
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
    int incorrectDuration = 0;
    int correctDuration = 1;
    Validator validator = Validation.buildDefaultValidatorFactory().getValidator();

    @Test
    public void userValidation_whenNullName_returnConstraintViolation() {
        Film film = new Film(correctId, nullName, correctDescription, correctReleaseDate, correctDuration);

        Set<ConstraintViolation<Film>> violations = validator.validate(film);
        ConstraintViolation<Film> violation = violations.stream().findFirst()
                .orElseThrow(() -> new RuntimeException("Отсутствует ошибка валидации"));

        Assertions.assertEquals(1, violations.size());
        Assertions.assertEquals("name", violation.getPropertyPath().toString());
    }

    @Test
    public void userValidation_whenBlankName_returnConstraintViolation() {
        Film film = new Film(correctId, blankName, correctDescription, correctReleaseDate, correctDuration);

        Set<ConstraintViolation<Film>> violations = validator.validate(film);
        ConstraintViolation<Film> violation = violations.stream().findFirst()
                .orElseThrow(() -> new RuntimeException("Отсутствует ошибка валидации"));

        Assertions.assertEquals(1, violations.size());
        Assertions.assertEquals("name", violation.getPropertyPath().toString());
    }

    @Test
    public void userValidation_whenNameOnlySpaces_returnConstraintViolation() {
        Film film = new Film(correctId, nameOnlySpaces, correctDescription, correctReleaseDate, correctDuration);

        Set<ConstraintViolation<Film>> violations = validator.validate(film);
        ConstraintViolation<Film> violation = violations.stream().findFirst()
                .orElseThrow(() -> new RuntimeException("Отсутствует ошибка валидации"));

        Assertions.assertEquals(1, violations.size());
        Assertions.assertEquals("name", violation.getPropertyPath().toString());
    }

    @Test
    public void userValidation_whenDescriptionMoreThan200Characters_returnConstraintViolation() {
        Film film = new Film(correctId, correctName, descriptionMore200Characters, correctReleaseDate, correctDuration);

        Set<ConstraintViolation<Film>> violations = validator.validate(film);
        ConstraintViolation<Film> violation = violations.stream().findFirst()
                .orElseThrow(() -> new RuntimeException("Отсутствует ошибка валидации"));

        Assertions.assertEquals(1, violations.size());
        Assertions.assertEquals("description", violation.getPropertyPath().toString());
    }

    @Test
    public void userValidation_whenIncorrectReleaseDate_returnConstraintViolation() {
        Film film = new Film(correctId, correctName, correctDescription, incorrectReleaseDate, correctDuration);

        Set<ConstraintViolation<Film>> violations = validator.validate(film);
        ConstraintViolation<Film> violation = violations.stream().findFirst()
                .orElseThrow(() -> new RuntimeException("Отсутствует ошибка валидации"));

        Assertions.assertEquals(1, violations.size());
        Assertions.assertEquals("releaseDate", violation.getPropertyPath().toString());
    }

    @Test
    public void userValidation_whenIncorrectDuration_returnConstraintViolation() {
        Film film = new Film(correctId, correctName, correctDescription, correctReleaseDate, incorrectDuration);

        Set<ConstraintViolation<Film>> violations = validator.validate(film);
        ConstraintViolation<Film> violation = violations.stream().findFirst()
                .orElseThrow(() -> new RuntimeException("Отсутствует ошибка валидации"));

        Assertions.assertEquals(1, violations.size());
        Assertions.assertEquals("duration", violation.getPropertyPath().toString());
    }

    @Test
    public void userValidation_whenAllFieldsCorrect_returnNoConstraintViolation() {
        Film film = new Film(correctId, correctName, correctDescription, correctReleaseDate, correctDuration);

        Set<ConstraintViolation<Film>> violations = validator.validate(film);

        Assertions.assertEquals(0, violations.size());
    }
}
