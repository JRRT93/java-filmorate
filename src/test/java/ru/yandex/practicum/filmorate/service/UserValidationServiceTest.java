package ru.yandex.practicum.filmorate.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import ru.yandex.practicum.filmorate.dao.InMemoryUserStorageTest;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.User;

import javax.validation.ConstraintViolation;
import javax.validation.Validation;
import javax.validation.Validator;
import javax.validation.ValidatorFactory;

import java.time.LocalDate;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

class UserValidationServiceTest extends UserValidationService {
    User user;
    private static final Validator validator;
    static {
        ValidatorFactory validatorFactory = Validation.buildDefaultValidatorFactory();
        validator = validatorFactory.usingContext().getValidator();

    }

    @BeforeEach
    void makeUser() {
        user = new User(1, "zahodim@yandex.ru", "GymBoss", "Van",
                LocalDate.of(1972, 10, 24));

    }

    @Test
    void validateUserLoginTests() throws ValidationException {
        this.validateUser(user);
        assertEquals(user, new User(1, "zahodim@yandex.ru", "GymBoss", "Van",
                LocalDate.of(1972, 10, 24)));

        user.setLogin("LOGIN WITH SPACES");
        assertThrows(ValidationException.class, () -> this.validateUser(user));

        user.setLogin(" ");
        assertThrows(ValidationException.class, () -> this.validateUser(user));
    }

    @Test
    void validateUserEmptyNameTest() {
        InMemoryUserStorageTest inMemoryUserStorageTest = new InMemoryUserStorageTest();
        user.setName(" ");
        user = inMemoryUserStorageTest.addUser(user);
        assertEquals(user, new User(1, "zahodim@yandex.ru", "GymBoss", "GymBoss",
                LocalDate.of(1972, 10, 24)));
    }

    @Test
    void validateUserEmailTests() {
        Set<ConstraintViolation<User>> violations = validator.validate(user);
        assertEquals(0, violations.size(), "Валидация zahodim@yandex.ru 100 не прошла");

        user.setEmail(" ");
        violations = validator.validate(user);
        assertEquals(2, violations.size(), "Валидация пустого емейла прошла");

        user.setEmail("кривособаков@");
        violations = validator.validate(user);
        assertEquals(1, violations.size(), "Валидация пустого некорректного емейла прошла");
    }

    @Test
    void validateUserBirthDayTests() {
        Set<ConstraintViolation<User>> violations = validator.validate(user);
        assertEquals(0, violations.size(), "Валидация 24.10.1972 не прошла");

        user.setBirthday(LocalDate.of(2072, 10, 24));
        violations = validator.validate(user);
        assertEquals(1, violations.size(), "Валидация 24.10.2072 прошла");
    }
}