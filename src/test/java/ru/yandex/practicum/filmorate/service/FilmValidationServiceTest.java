package ru.yandex.practicum.filmorate.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.Film;

import javax.validation.ConstraintViolation;
import javax.validation.Validation;
import javax.validation.Validator;
import javax.validation.ValidatorFactory;
import java.time.LocalDate;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

class FilmValidationServiceTest extends FilmValidationService {
    Film film;
    private static Validator validator;
        static {
            ValidatorFactory validatorFactory = Validation.buildDefaultValidatorFactory();
            validator = validatorFactory.usingContext().getValidator();

    }


    @BeforeEach
    void makeUser() {
        film = new Film(1, "Мочилово", "ЗДЕСЬ МЕНЬШЕ 200! приезжают в город Бризуль. З" +
                "десь они хотят разыскать господина Огюста Куглова, который задолжал им деньги",
                100, LocalDate.of(2000, 1, 1));
    }


    @Test
    void validateFilmDescriptionTests() throws ValidationException {
        this.validateFilm(film);
        assertEquals(film, new Film(1, "Мочилово", "ЗДЕСЬ МЕНЬШЕ 200! приезжают в город Бризуль. З" +
                "десь они хотят разыскать господина Огюста Куглова, который задолжал им деньги",
                100, LocalDate.of(2000, 1, 1)));

        film.setDescription("ЗДЕСЬ РОВНО 200! Пятеро друзей ( комик-группа «Шарло»), приезжают в город Бризуль. " +
                "Здесь они хотят разыскать " +
                "господина Огюста Куглова, который задолжал им деньги, а именно 20 миллионов. о КугловАААААА");
        this.validateFilm(film);
        assertEquals(film, new Film(1, "Мочилово", "ЗДЕСЬ РОВНО 200! Пятеро друзей " +
                "( комик-группа «Шарло»), приезжают в город Бризуль. Здесь они хотят разыскать " +
                "господина Огюста Куглова, который задолжал им деньги, а именно 20 миллионов. о КугловАААААА",
                100, LocalDate.of(2000, 1, 1)));

        film.setDescription("ЗДЕСЬ БОЛЬШЕ 200! Пятеро друзей ( комик-группа «Шарло»), приезжают в город Бризуль. Здесь они хотят разыскать " +
                "господина Огюста Куглова, который задолжал им деньги, а именно 20 миллионов. о Куглов, который за время" +
                " «своего отсутствия», стал кандидатом Коломбани.");
        assertThrows(ValidationException.class, () -> this.validateFilm(film));
    }

    @Test
    void validateFilmReleaseDateTests() throws ValidationException {
        this.validateFilm(film);
        assertEquals(film, new Film(1, "Мочилово", "ЗДЕСЬ МЕНЬШЕ 200! приезжают в город Бризуль. З" +
                "десь они хотят разыскать господина Огюста Куглова, который задолжал им деньги",
                100, LocalDate.of(2000, 1, 1)));

        film.setReleaseDate(LocalDate.of(1895, 12, 28));
        assertEquals(film, new Film(1, "Мочилово", "ЗДЕСЬ МЕНЬШЕ 200! приезжают в город Бризуль. З" +
                "десь они хотят разыскать господина Огюста Куглова, который задолжал им деньги",
                100, LocalDate.of(1895, 12, 28)));

        film.setReleaseDate(LocalDate.of(1895, 12, 27));
        assertThrows(ValidationException.class, () -> this.validateFilm(film));
    }

    @Test
    void validateFilmDurationTests() {
        Set<ConstraintViolation<Film>> violations = validator.validate(film);
        assertEquals(0, violations.size(), "Валидация продолжительности 100 не прошла");

        film.setDuration(0);
        violations = validator.validate(film);
        assertEquals(1, violations.size(), "Валидация продолжительности 0 прошла");

        film.setDuration(-100);
        violations = validator.validate(film);
        assertEquals(1, violations.size(), "Валидация продолжительности -100 прошла");
    }

    @Test
    void validateFilmNameTests() {
        Set<ConstraintViolation<Film>> violations = validator.validate(film);
        assertEquals(0, violations.size(), "Валидация name Мочилово не прошла");

        film.setName(" ");
        violations = validator.validate(film);
        assertEquals(1, violations.size(), "Валидация пустого name прошла");
    }
}