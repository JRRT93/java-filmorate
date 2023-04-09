package ru.yandex.practicum.filmorate.annotation;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

public class LengthLimitValidator implements ConstraintValidator<LengthLimit, String> {
    @Override
    public boolean isValid(String value, ConstraintValidatorContext context) {
        if (value != null && !value.isEmpty()) {
            return value.length() < 200;
        }
        return true;
    }
}
