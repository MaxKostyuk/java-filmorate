package ru.yandex.practicum.filmorate;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import ru.yandex.practicum.filmorate.model.User;

import javax.validation.ConstraintViolation;
import javax.validation.Validation;
import javax.validation.Validator;
import java.time.LocalDate;
import java.util.Set;

public class UserValidationTest {

    private int correctId = 1;
    private String nullEmail = null;
    private String blankEmail = "";
    private String incorrectEmail = "example.com@";
    private String correctEmail = "example@gmail.com";
    private String blankLogin = "";
    private String loginWithSpaces = "login incorrect";
    private String correctLogin = "login";
    private LocalDate incorrectBirthday = LocalDate.now().plusDays(1);
    private LocalDate correctBirthday = LocalDate.now();
    private Validator validator = Validation.buildDefaultValidatorFactory().getValidator();


    @Test
    public void userValidation_whenNullEmail_returnConstraintViolation() {
        User user = new User(correctId, nullEmail, correctLogin, correctBirthday);

        Set<ConstraintViolation<User>> violations = validator.validate(user);
        ConstraintViolation<User> violation = violations.stream().findFirst()
                .orElseThrow(() -> new RuntimeException("Отсутствует ошибка валидации"));

        Assertions.assertEquals(1, violations.size());
        Assertions.assertEquals("email", violation.getPropertyPath().toString());
    }

    @Test
    public void userValidation_whenBlankEmail_returnConstraintViolation() {
        User user = new User(correctId, blankEmail, correctLogin, correctBirthday);

        Set<ConstraintViolation<User>> violations = validator.validate(user);
        ConstraintViolation<User> violation = violations.stream().findFirst()
                .orElseThrow(() -> new RuntimeException("Отсутствует ошибка валидации"));

        Assertions.assertEquals(1, violations.size());
        Assertions.assertEquals("email", violation.getPropertyPath().toString());
    }

    @Test
    public void userValidation_whenIncorrectEmail_returnConstraintViolation() {
        User user = new User(correctId, incorrectEmail, correctLogin, correctBirthday);

        Set<ConstraintViolation<User>> violations = validator.validate(user);
        ConstraintViolation<User> violation = violations.stream().findFirst()
                .orElseThrow(() -> new RuntimeException("Отсутствует ошибка валидации"));

        Assertions.assertEquals(1, violations.size());
        Assertions.assertEquals("email", violation.getPropertyPath().toString());
    }

    @Test
    public void userValidation_whenBlankLogin_returnConstraintViolation() {
        User user = new User(correctId, correctEmail, blankLogin, correctBirthday);

        Set<ConstraintViolation<User>> violations = validator.validate(user);
        ConstraintViolation<User> violation = violations.stream().findFirst()
                .orElseThrow(() -> new RuntimeException("Отсутствует ошибка валидации"));

        Assertions.assertEquals(1, violations.size());
        Assertions.assertEquals("login", violation.getPropertyPath().toString());
    }

    @Test
    public void userValidation_whenLoginWithSpaces_returnConstraintViolation() {
        User user = new User(correctId, correctEmail, loginWithSpaces, correctBirthday);

        Set<ConstraintViolation<User>> violations = validator.validate(user);
        ConstraintViolation<User> violation = violations.stream().findFirst()
                .orElseThrow(() -> new RuntimeException("Отсутствует ошибка валидации"));

        Assertions.assertEquals(1, violations.size());
        Assertions.assertEquals("login", violation.getPropertyPath().toString());
    }

    @Test
    public void userValidation_whenIncorrectBirthday_returnConstraintViolation() {
        User user = new User(correctId, correctEmail, correctLogin, incorrectBirthday);

        Set<ConstraintViolation<User>> violations = validator.validate(user);
        ConstraintViolation<User> violation = violations.stream().findFirst()
                .orElseThrow(() -> new RuntimeException("Отсутствует ошибка валидации"));

        Assertions.assertEquals(1, violations.size());
        Assertions.assertEquals("birthday", violation.getPropertyPath().toString());
    }

    @Test
    public void userValidation_whenAllFieldsCorrect_returnNoConstraintViolation() {
        User user = new User(correctId, correctEmail, correctLogin, correctBirthday);

        Set<ConstraintViolation<User>> violations = validator.validate(user);

        Assertions.assertEquals(0, violations.size());
    }
}
