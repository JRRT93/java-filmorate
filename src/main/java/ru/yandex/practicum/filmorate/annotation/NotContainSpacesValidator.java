package ru.yandex.practicum.filmorate.annotation;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

public class NotContainSpacesValidator implements ConstraintValidator<NotContainSpaces, String> {
    @Override
    public boolean isValid(String value, ConstraintValidatorContext context) {
        if (value != null && !value.isEmpty()) {
            return !value.contains(" ");
        }
        return true;
    }
}
