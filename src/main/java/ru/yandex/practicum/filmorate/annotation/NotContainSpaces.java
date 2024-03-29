package ru.yandex.practicum.filmorate.annotation;

import javax.validation.Constraint;
import javax.validation.Payload;
import java.lang.annotation.Documented;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import static java.lang.annotation.ElementType.FIELD;

@Target({FIELD})
@Retention(RetentionPolicy.RUNTIME)
@Constraint(validatedBy = NotContainSpacesValidator.class)
@Documented

public @interface NotContainSpaces {
    String message() default "{NotContainSpaces.invalid}";

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};
}
