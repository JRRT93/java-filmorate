package ru.yandex.practicum.filmorate.service;

import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.jdbc.core.JdbcTemplate;
import ru.yandex.practicum.filmorate.dao.GenreDao;
import ru.yandex.practicum.filmorate.dao.GenreDaoImpl;
import ru.yandex.practicum.filmorate.dao.RatingDao;
import ru.yandex.practicum.filmorate.dao.RatingDaoImpl;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.model.Rating;
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
    private final RatingDao ratingDao = new RatingDaoImpl(new JdbcTemplate());
    private final GenreDao genreDao = new GenreDaoImpl(new JdbcTemplate());
    private Film film1;
    private static final Validator validator;

    static {
        ValidatorFactory validatorFactory = Validation.buildDefaultValidatorFactory();
        validator = validatorFactory.usingContext().getValidator();
    }

    @BeforeEach
    void makeFilms() {
        film1 = new Film(null, "ПЕРВЫЙ ФИЛЬМ", "ОПИСАНИЕ МЕНЬШЕ 200 СИМВОЛОВ", 60,
                LocalDate.of(2000, 1, 1), null, new Rating((long) 2, "Ужасы"),
                null);
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
        assertNotNull(this.findFilmById(1).getId());
        assertNotNull(this.findFilmById(1).getLikes());
        assertEquals("ПЕРВЫЙ ФИЛЬМ", this.findFilmById(1).getName(), "Название изменилось");
        assertEquals("ОПИСАНИЕ МЕНЬШЕ 200 СИМВОЛОВ", this.findFilmById(1).getDescription(),
                "Описание изменилось");
        assertEquals(60, this.findFilmById(1).getDuration(), "Продолжительность изменилось");
        assertEquals(LocalDate.of(2000, 1, 1), this.findFilmById(1).getReleaseDate(),
                "Дата релиза изменилась");
    }

    @Test
    void updateFilmShouldUpdateAllInitializedFields() throws ValidationException {
        Film updatedFilm = new Film((long) 1, "UPDATE ВТОРОЙ ФИЛЬМ", "UPDATE ОПИСАНИЕ МЕНЬШЕ 200 СИМВОЛОВ",
                120, LocalDate.of(2010, 2, 2), null, new Rating((long) 1, "Ужас"),
                null);
        addFilm(film1);
        updateFilm(updatedFilm);
        assertNotNull(this.findFilmById(1).getLikes());
        assertEquals(1, this.findFilmById(1).getId(), "ID изменилось");
        assertEquals("UPDATE ВТОРОЙ ФИЛЬМ", this.findFilmById(1).getName(), "Название не изменилось");
        assertEquals("UPDATE ОПИСАНИЕ МЕНЬШЕ 200 СИМВОЛОВ", this.findFilmById(1).getDescription(),
                "Описание не изменилось");
        assertEquals(120, this.findFilmById(1).getDuration(), "Продолжительность не изменилось");
        assertEquals(LocalDate.of(2010, 2, 2), this.findFilmById(1).getReleaseDate(),
                "Дата релиза не изменилась");
    }

    @Test
    void updateFilmShouldThrowExpDueInvalidID() {
        Film updatedFilm = new Film((long) 9999, "UPDATE ВТОРОЙ ФИЛЬМ", "UPDATE ОПИСАНИЕ МЕНЬШЕ 200 СИМВОЛОВ",
                120, LocalDate.of(2010, 2, 2), null, new Rating((long) 1, "Ужас"),
                null);
        addFilm(film1);
        assertThrows(ValidationException.class, () -> this.updateFilm(updatedFilm));
    }

    @Test
    void updateFilmShouldKeepInitialized() throws ValidationException {
        Set<Long> testSet = new TreeSet<>();
        testSet.add((long) 14);
        Film updatedFilm = new Film((long) 1, "UPDATE ВТОРОЙ ФИЛЬМ", "UPDATE ОПИСАНИЕ МЕНЬШЕ 200 СИМВОЛОВ",
                120, LocalDate.of(2010, 2, 2), testSet, new Rating((long) 1, "Ужас"),
                null);
        addFilm(film1);
        updateFilm(updatedFilm);
        assertEquals(testSet, this.findFilmById(1).getLikes(), "LIKES изменилось");
    }

    @Test
    void findPopularFilmLessTenFilmsCountNotSpecifiedAndSpecified() {
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
        Film film2 = new Film((long) 2, "ВТОРОЙ ФИЛЬМ", "ОПИСАНИЕ МЕНЬШЕ 200 СИМВОЛОВ", 120,
                LocalDate.of(2010, 2, 2), likeSet2, null, null);
        Film film3 = new Film((long) 3, "ТРЕТИЙ ФИЛЬМ", "ОПИСАНИЕ МЕНЬШЕ 200 СИМВОЛОВ", 180,
                LocalDate.of(2020, 3, 3), likeSet3, null, null);
        Film film4 = new Film((long) 4, "ВТОРОЙ ФИЛЬМ", "ОПИСАНИЕ МЕНЬШЕ 200 СИМВОЛОВ", 120,
                LocalDate.of(2030, 4, 4), likeSet4, null, null);
        Film film5 = new Film((long) 5, "ТРЕТИЙ ФИЛЬМ", "ОПИСАНИЕ МЕНЬШЕ 200 СИМВОЛОВ", 180,
                LocalDate.of(2040, 5, 5), likeSet5, null, null);
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

    @Test
    void findPopularFilmMoreTenFilmsCountNotSpecifiedAndSpecified() {
        Set<Long> likeSet2 = new TreeSet<>();
        Set<Long> likeSet3 = new TreeSet<>();
        Set<Long> likeSet4 = new TreeSet<>();
        Set<Long> likeSet5 = new TreeSet<>();
        Set<Long> likeSet6 = new TreeSet<>();
        Set<Long> likeSet7 = new TreeSet<>();
        Set<Long> likeSet8 = new TreeSet<>();
        Set<Long> likeSet9 = new TreeSet<>();
        Set<Long> likeSet10 = new TreeSet<>();
        Set<Long> likeSet11 = new TreeSet<>();

        likeSet11.add((long) 1);
        likeSet11.add((long) 2);
        likeSet11.add((long) 3);
        likeSet11.add((long) 4);
        likeSet11.add((long) 5);
        likeSet11.add((long) 6);
        likeSet11.add((long) 7);
        likeSet11.add((long) 8);
        likeSet11.add((long) 9);
        likeSet11.add((long) 10);

        likeSet10.add((long) 1);
        likeSet10.add((long) 2);
        likeSet10.add((long) 3);
        likeSet10.add((long) 4);
        likeSet10.add((long) 5);
        likeSet10.add((long) 6);
        likeSet10.add((long) 7);
        likeSet10.add((long) 8);
        likeSet10.add((long) 9);

        likeSet9.add((long) 1);
        likeSet9.add((long) 2);
        likeSet9.add((long) 3);
        likeSet9.add((long) 4);
        likeSet9.add((long) 5);
        likeSet9.add((long) 6);
        likeSet9.add((long) 7);
        likeSet9.add((long) 8);

        likeSet5.add((long) 1);
        likeSet5.add((long) 2);
        likeSet5.add((long) 3);
        likeSet5.add((long) 4);
        likeSet5.add((long) 5);
        likeSet5.add((long) 6);
        likeSet5.add((long) 7);

        likeSet8.add((long) 1);
        likeSet8.add((long) 2);
        likeSet8.add((long) 3);
        likeSet8.add((long) 4);
        likeSet8.add((long) 5);
        likeSet8.add((long) 6);

        likeSet7.add((long) 1);
        likeSet7.add((long) 2);
        likeSet7.add((long) 3);
        likeSet7.add((long) 4);
        likeSet7.add((long) 5);

        likeSet6.add((long) 1);
        likeSet6.add((long) 2);
        likeSet6.add((long) 3);
        likeSet6.add((long) 4);

        likeSet4.add((long) 1);
        likeSet4.add((long) 2);
        likeSet4.add((long) 3);

        likeSet3.add((long) 1);
        likeSet3.add((long) 2);

        likeSet2.add((long) 1);

        Film film2 = new Film((long) 2, "ВТОРОЙ ФИЛЬМ", "ОПИСАНИЕ МЕНЬШЕ 200 СИМВОЛОВ", 120,
                LocalDate.of(2010, 2, 2), likeSet2, null, null);
        Film film3 = new Film((long) 3, "ТРЕТИЙ ФИЛЬМ", "ОПИСАНИЕ МЕНЬШЕ 200 СИМВОЛОВ", 180,
                LocalDate.of(2020, 3, 3), likeSet3, null, null);
        Film film4 = new Film((long) 4, "ВТОРОЙ ФИЛЬМ", "ОПИСАНИЕ МЕНЬШЕ 200 СИМВОЛОВ", 120,
                LocalDate.of(2030, 4, 4), likeSet4, null, null);
        Film film5 = new Film((long) 5, "ТРЕТИЙ ФИЛЬМ", "ОПИСАНИЕ МЕНЬШЕ 200 СИМВОЛОВ", 180,
                LocalDate.of(2040, 5, 5), likeSet5, null, null);
        Film film6 = new Film((long) 2, "ВТОРОЙ ФИЛЬМ", "ОПИСАНИЕ МЕНЬШЕ 200 СИМВОЛОВ", 120,
                LocalDate.of(2010, 6, 6), likeSet6, null, null);
        Film film7 = new Film((long) 3, "ТРЕТИЙ ФИЛЬМ", "ОПИСАНИЕ МЕНЬШЕ 200 СИМВОЛОВ", 180,
                LocalDate.of(2020, 7, 7), likeSet7, null, null);
        Film film8 = new Film((long) 4, "ВТОРОЙ ФИЛЬМ", "ОПИСАНИЕ МЕНЬШЕ 200 СИМВОЛОВ", 120,
                LocalDate.of(2030, 8, 8), likeSet8, null, null);
        Film film9 = new Film((long) 5, "ТРЕТИЙ ФИЛЬМ", "ОПИСАНИЕ МЕНЬШЕ 200 СИМВОЛОВ", 180,
                LocalDate.of(2040, 9, 9), likeSet9, null, null);
        Film film10 = new Film((long) 4, "ВТОРОЙ ФИЛЬМ", "ОПИСАНИЕ МЕНЬШЕ 200 СИМВОЛОВ", 120,
                LocalDate.of(2030, 10, 10), likeSet10, null, null);
        Film film11 = new Film((long) 5, "ТРЕТИЙ ФИЛЬМ", "ОПИСАНИЕ МЕНЬШЕ 200 СИМВОЛОВ", 180,
                LocalDate.of(2040, 11, 11), likeSet11, null, null);

        addFilm(film1);
        addFilm(film2);
        addFilm(film3);
        addFilm(film4);
        addFilm(film5);
        addFilm(film6);
        addFilm(film7);
        addFilm(film8);
        addFilm(film9);
        addFilm(film10);
        addFilm(film11);

        List<Film> list = findPopularFilms(0);
        List<Film> expectedList = List.of(film11, film10, film9, film5, film8, film7, film6, film4, film3, film2);
        assertEquals(expectedList, list, "Метод не работает");

        list = findPopularFilms(5);
        expectedList = List.of(film11, film10, film9, film5, film8);
        assertEquals(expectedList, list, "Метод не работает");
    }

    public Film addFilm(Film film) {
        if (film.getLikes() == null) film.setLikes(new TreeSet<>());
        log.debug("LIKES field initialized");
        if (film.getGenres() == null) film.setGenres(new TreeSet<>());
        log.debug("GENRES field initialized");
        film.setId(fIlmStorage.addFilm(film));
        log.debug("FILM successful added. ID=" + film.getId());
        return film;
    }

    public Film updateFilm(Film film) throws ValidationException {
        fIlmStorage.findFilmById(film.getId());
        if (film.getLikes() == null) film.setLikes(new TreeSet<>());
        log.debug("LIKES field initialized");
        if (film.getGenres() == null) film.setGenres(new TreeSet<>());
        log.debug("GENRES field initialized");
        fIlmStorage.updateFilm(film);
        log.debug("FILM successful updated. ID=" + film.getId());
        return film;
    }

    public List<Film> getAllFilms() {
        return fIlmStorage.getAllFilms();
    }

    public Film findFilmById(long id) throws ValidationException {
        return fIlmStorage.findFilmById(id);
    }

    public void addFilmLike(long id, long userId) throws ValidationException {
        Film film = fIlmStorage.findFilmById(id);
        film.getLikes().add(userId);
        if (fIlmStorage.updateFilm(film)) {
            log.debug("LIKE for film ID=" + id + " from User ID=" + userId + " added");
        }
    }

    public void deleteFilmLike(long id, long userId) throws ValidationException {
        Film film = fIlmStorage.findFilmById(id);
        fIlmStorage.findFilmById(id).getLikes().remove(userId);
        if (fIlmStorage.updateFilm(film)) {
            log.debug("LIKE for film ID=" + id + " from User ID=" + userId + " deleted");
        }
    }

    public List<Film> findPopularFilms(int count) {
        List<Film> popularFilms = new ArrayList<>();
        Comparator<Film> comparator = (Film film1, Film film2) -> film2.getLikes().size() - (film1.getLikes().size());
        List<Film> sortedList = fIlmStorage.getAllFilms();
        sortedList.sort(comparator);
        if (count == 0) {
            if (sortedList.size() <= 10) {
                log.debug("LIST OF POPULAR FILMS provided. No COUNT requested, list SIZE=" + sortedList.size());
                return sortedList;
            }
            for (int i = 0; i < 10; i++) {
                popularFilms.add(sortedList.get(i));
            }
            log.debug("LIST OF POPULAR FILMS provided. No COUNT requested, list SIZE=" + popularFilms.size());
        } else {
            for (int i = 0; i < count; i++) {
                popularFilms.add(sortedList.get(i));
            }
            log.debug("LIST OF POPULAR FILMS provided. Requested COUNT=" + count + ", list SIZE=" + popularFilms.size());
        }
        return popularFilms;
    }

    public List<Genre> getAllGenres() {
        return genreDao.getAllGenres();
    }

    public Genre findGenreById(long id) throws ValidationException {
        return genreDao.findGenreById(id);
    }

    public List<Rating> getAllRatings() {
        return ratingDao.getAllRatings();
    }

    public Rating findRatingById(long id) throws ValidationException {
        return ratingDao.findRatingById(id);
    }
}