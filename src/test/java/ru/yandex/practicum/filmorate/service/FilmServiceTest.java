package ru.yandex.practicum.filmorate.service;

import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.storage.FIlmStorage;
import ru.yandex.practicum.filmorate.storage.InMemoryFilmStorageTest;


import javax.validation.ConstraintViolation;
import javax.validation.Validation;
import javax.validation.Validator;
import javax.validation.ValidatorFactory;
import java.time.LocalDate;
import java.util.*;

import static org.junit.jupiter.api.Assertions.*;

@Slf4j
class FilmServiceTest {
    private final FIlmStorage fIlmStorage = new InMemoryFilmStorageTest();
    private long id = 1;
    private Film film1;
    private static Validator validator;
        static {
            ValidatorFactory validatorFactory = Validation.buildDefaultValidatorFactory();
            validator = validatorFactory.usingContext().getValidator();
    }

    @BeforeEach
    void makeFilms() {
        film1 = new Film(null, "ПЕРВЫЙ ФИЛЬМ", "ОПИСАНИЕ МЕНЬШЕ 200 СИМВОЛОВ", 60,
                LocalDate.of(2000, 1, 1), null);
    }

    @Test
    void durationValidationShouldFindViolations() {
        Set<ConstraintViolation<Film>> violations = validator.validate(film1);
        assertEquals(0, violations.size(), "Валидация продолжительности 100 не прошла");

        film1.setDuration(0);
        violations = validator.validate(film1);
        assertEquals(1, violations.size(), "Валидация продолжительности 0 прошла");

        film1.setDuration(-100);
        violations = validator.validate(film1);
        assertEquals(1, violations.size(), "Валидация продолжительности -100 прошла");
    }

    @Test
    void nameValidationShouldFindViolations() {
        Set<ConstraintViolation<Film>> violations = validator.validate(film1);
        assertEquals(0, violations.size(), "Валидация name Мочилово не прошла");

        film1.setName(" ");
        violations = validator.validate(film1);
        assertEquals(1, violations.size(), "Валидация пустого name прошла");
    }

    @Test
    void descriptionValidationShouldFindViolations() {
        Set<ConstraintViolation<Film>> violations = validator.validate(film1);
        assertEquals(0, violations.size(), "Валидация description ОПИСАНИЕ МЕНЬШЕ 200 СИМВОЛОВ не прошла");

        film1.setDescription("ЗДЕСЬ РОВНО 200! Пятеро друзей ( комик-группа «Шарло»), приезжают в город Бризуль. " +
                "Здесь они хотят разыскать господина Огюста Куглова, который задолжал им деньги, а именно 20 миллионов. " +
                "о КугловАААААА");
        violations = validator.validate(film1);
        assertEquals(1, violations.size(), "Валидация description 200 прошла");

        film1.setDescription("ЗДЕСЬ БОЛЬШЕ 200! Пятеро друзей ( комик-группа «Шарло»), приезжают в город Бризуль. " +
                "Здесь они хотят разыскать " +
                "господина Огюста Куглова, который задолжал им деньги, а именно 20 миллионов. о Куглов, который за время" +
                " «своего отсутствия», стал кандидатом Коломбани.");
        violations = validator.validate(film1);
        assertEquals(1, violations.size(), "Валидация description >200 прошла");
    }

    @Test
    void releaseDateValidationShouldFindViolations() {
        Set<ConstraintViolation<Film>> violations = validator.validate(film1);
        assertEquals(0, violations.size(), "Валидация duration 60 не прошла");

        film1.setDuration(0);
        violations = validator.validate(film1);
        assertEquals(1, violations.size(), "Валидация duration 0 прошла");

        film1.setDuration(-60);
        violations = validator.validate(film1);
        assertEquals(1, violations.size(), "Валидация duration -60 прошла");
    }


    @Test
    void addFilmIDShouldAssignedLikesInitialized() throws ValidationException {
            this.addFilm(film1);
            assertNotNull(this.findFilmByID(1).getId());
            assertNotNull(this.findFilmByID(1).getLikes());
            assertEquals("ПЕРВЫЙ ФИЛЬМ", this.findFilmByID(1).getName(),"Название изменилось");
            assertEquals("ОПИСАНИЕ МЕНЬШЕ 200 СИМВОЛОВ", this.findFilmByID(1).getDescription(),
                    "Описание изменилось");
            assertEquals(60, this.findFilmByID(1).getDuration(),"Продолжительность изменилось");
            assertEquals(LocalDate.of(2000, 1, 1), this.findFilmByID(1).getReleaseDate(),
            "Дата релиза изменилась");
    }

    @Test
    void updateFilmShouldUpdateAllInitializedFields () throws ValidationException {
            Film updatedFilm = new Film((long)1, "UPDATE ВТОРОЙ ФИЛЬМ", "UPDATE ОПИСАНИЕ МЕНЬШЕ 200 СИМВОЛОВ",
                    120, LocalDate.of(2010, 2, 2), null);
            addFilm(film1);
            updateFilm(updatedFilm);
            assertNotNull(this.findFilmByID(1).getLikes());
            assertEquals(1,this.findFilmByID(1).getId(), "ID изменилось");
            assertEquals("UPDATE ВТОРОЙ ФИЛЬМ", this.findFilmByID(1).getName(),"Название не изменилось");
            assertEquals("UPDATE ОПИСАНИЕ МЕНЬШЕ 200 СИМВОЛОВ", this.findFilmByID(1).getDescription(),
                "Описание не изменилось");
            assertEquals(120, this.findFilmByID(1).getDuration(),"Продолжительность не изменилось");
        assertEquals(LocalDate.of(2010, 2, 2), this.findFilmByID(1).getReleaseDate(),
                "Дата релиза не изменилась");
    }

    @Test
    void updateFilmShouldThrowExpDueInvalidID () throws ValidationException {
        Film updatedFilm = new Film((long)9999, "UPDATE ВТОРОЙ ФИЛЬМ", "UPDATE ОПИСАНИЕ МЕНЬШЕ 200 СИМВОЛОВ",
                120, LocalDate.of(2010, 2, 2), null);
        addFilm(film1);
        assertThrows(ValidationException.class, () -> this.updateFilm(updatedFilm));
    }

    @Test
    void updateFilmShouldKeepInitialized () throws ValidationException {
            Set<Long> testSet = new TreeSet<>();
            testSet.add((long)14);
        Film updatedFilm = new Film((long)1, "UPDATE ВТОРОЙ ФИЛЬМ", "UPDATE ОПИСАНИЕ МЕНЬШЕ 200 СИМВОЛОВ",
                120, LocalDate.of(2010, 2, 2), testSet);
        addFilm(film1);
        updateFilm(updatedFilm);
        assertEquals(testSet,this.findFilmByID(1).getLikes(), "LIKES изменилось");
    }

    @Test
    void findPopularFilmCountNotSpecified() {
        Set<Long> likeSet2 = new TreeSet<>();
        Set<Long> likeSet3 = new TreeSet<>();
        Set<Long> likeSet4 = new TreeSet<>();
        Set<Long> likeSet5 = new TreeSet<>();
        likeSet5.add((long) 1);
        likeSet5.add((long) 2);
        likeSet4.add((long) 1);
        likeSet4.add((long) 2);
        likeSet4.add((long) 3);
        likeSet3.add((long) 1);
        likeSet2.add((long) 2);
        likeSet2.add((long) 1);
        likeSet2.add((long) 3);
        likeSet2.add((long) 4);
        likeSet2.add((long) 5);
        likeSet2.add((long) 88);
        Film film2 = new Film((long)2, "ВТОРОЙ ФИЛЬМ", "ОПИСАНИЕ МЕНЬШЕ 200 СИМВОЛОВ", 120,
                LocalDate.of(2010, 2, 2), likeSet2);
        Film film3 = new Film((long)3, "ТРЕТИЙ ФИЛЬМ", "ОПИСАНИЕ МЕНЬШЕ 200 СИМВОЛОВ", 180,
                LocalDate.of(2020, 3, 3), likeSet3);
        Film film4 = new Film((long)4, "ВТОРОЙ ФИЛЬМ", "ОПИСАНИЕ МЕНЬШЕ 200 СИМВОЛОВ", 120,
                LocalDate.of(2030, 4, 4), likeSet4);
        Film film5 = new Film((long)5, "ТРЕТИЙ ФИЛЬМ", "ОПИСАНИЕ МЕНЬШЕ 200 СИМВОЛОВ", 180,
                LocalDate.of(2040, 5, 5), likeSet5);
        addFilm(film1);
        addFilm(film2);
        addFilm(film3);
        addFilm(film4);
        addFilm(film5);
        List<Film> list = findPopularFilms(0);
        List<Film> expectedList = List.of(film2, film4, film5, film3, film1);
        assertEquals(expectedList, list, "Метод не работает");

        list = findPopularFilms(3);
        expectedList = List.of(film2, film4, film5);
        assertEquals(expectedList, list, "Метод не работает");
    }

    public Film addFilm (Film film) {
        film.setId(id);
        id++;
        fIlmStorage.addFilm(film);
        log.debug("FILM successful added. ID=" + film.getId());
        return film;
    }

    public Film updateFilm(Film film) throws ValidationException {
        fIlmStorage.updateFilm(film);
        log.debug("FILM successful updated. ID=" + film.getId());
        return film;
    }

    public List<Film> getAllFilms() {
        return fIlmStorage.getAllFilms();
    }

    public Film findFilmByID(long id) throws ValidationException {
        return fIlmStorage.findFilmByID(id);
    }

    public void addFilmLike(long id, long userID) throws ValidationException {
        fIlmStorage.findFilmByID(id).getLikes().add(userID);
    }

    public void deleteFilmLike(long id, long userID) throws ValidationException {
        fIlmStorage.findFilmByID(id).getLikes().remove(userID);
    }

    public List<Film> findPopularFilms (int count) {
        List<Film> popularFilms = new ArrayList<>();
        Comparator<Film> comparator = (Film film1, Film film2) -> film2.getLikes().size() - (film1.getLikes().size());
        List<Film> sortedList = fIlmStorage.getAllFilms();
        sortedList.sort(comparator);
        if (count == 0) {
            if (sortedList.size() <= 10) return sortedList;
            for (int i = 0; i < 10; i++) {
                popularFilms.add(sortedList.get(i));
            }
        } else {
            for (int i = 0; i < count; i++) {
                popularFilms.add(sortedList.get(i));
            }
        }
        return popularFilms;
    }
}