package ru.yandex.practicum.filmorate.annotation;
import javax.validation.Constraint;
import javax.validation.Payload;
import java.lang.annotation.Documented;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import static java.lang.annotation.ElementType.FIELD;

@Target({ FIELD })
@Retention(RetentionPolicy.RUNTIME)
@Constraint(validatedBy = LengthLimitValidator.class)
@Documented
public @interface LengthLimit {
    String message() default "Incorrect film description: maximum length is 200";

    Class<?>[] groups() default { };

    Class<? extends Payload>[] payload() default { };
}
