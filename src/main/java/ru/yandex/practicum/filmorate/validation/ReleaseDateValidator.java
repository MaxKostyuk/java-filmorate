package ru.yandex.practicum.filmorate.validation;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;
import java.time.LocalDate;
import java.time.Month;

public class ReleaseDateValidator implements ConstraintValidator<ReleaseDateValidation, LocalDate> {

    private static final LocalDate FIRST_FILM_DEMONSTRATION = LocalDate.of(1895, Month.DECEMBER, 28);


    @Override
    public boolean isValid(LocalDate releaseDate, ConstraintValidatorContext constraintValidatorContext) {
        return releaseDate.isAfter(FIRST_FILM_DEMONSTRATION);
    }
}
