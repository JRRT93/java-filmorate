package ru.yandex.practicum.filmorate.model.dao.annotation;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;
import java.time.LocalDate;

public class ReleaseDateLimitValidator implements ConstraintValidator<ReleaseDateLimit, LocalDate> {
    @Override
    public boolean isValid(LocalDate value, ConstraintValidatorContext context) {
        if (value != null) {
            LocalDate limitDate = LocalDate.of(1895, 12, 28);
            return value.isAfter(limitDate);
        }
        return false;
    }
}
