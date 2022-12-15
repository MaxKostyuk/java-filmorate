package ru.yandex.practicum.filmorate.validation;

import javax.validation.Constraint;
import javax.validation.Payload;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target(ElementType.FIELD)
@Retention(RetentionPolicy.RUNTIME)
@Constraint(validatedBy = ContainsNoSpacesValidator.class)
public @interface ContainsNoSpaces {
    String message() default "Login must contain no spaces";

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};
}
