package ru.yandex.practicum.filmorate;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.datasource.embedded.EmbeddedDatabase;
import org.springframework.jdbc.datasource.embedded.EmbeddedDatabaseBuilder;
import org.springframework.jdbc.datasource.embedded.EmbeddedDatabaseType;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.model.Rating;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.dao.FilmDaoImpl;
import ru.yandex.practicum.filmorate.dao.GenreDaoImpl;
import ru.yandex.practicum.filmorate.dao.RatingDaoImpl;
import ru.yandex.practicum.filmorate.dao.UserDaoImpl;
import ru.yandex.practicum.filmorate.service.FilmService;
import ru.yandex.practicum.filmorate.service.UserService;
import ru.yandex.practicum.filmorate.storage.FilmDbStorage;
import ru.yandex.practicum.filmorate.storage.UserDbStorage;

import java.time.LocalDate;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@AutoConfigureTestDatabase
class FilmorateApplicationTests {
    private UserService userService;
    private FilmService filmService;
    private JdbcTemplate jdbcTemplate;
    private EmbeddedDatabase embeddedDatabase;

    public static void main(String[] args) {
        SpringApplication.run(FilmorateApplication.class, args);
    }

    @Test
    void contextLoads() {
    }

    @Test
    void getByIdAndGetAllUsersAndFilms() throws ValidationException {
        User user = userService.findUserById(1);
        assertNotNull(user.getId());
        assertNotNull(user.getFriends());
        assertEquals("Van", user.getName(), "Name изменилось");
        assertEquals("GymBoss", user.getLogin(), "Login изменилось");
        assertEquals("first@yandex.ru", user.getEmail(), "Email изменилось");
        assertEquals(LocalDate.of(1950, 10, 24), user.getBirthday(),
                "Birthday изменилась");

        Film film = filmService.findFilmById(2);
        assertNotNull(film.getId());
        assertNotNull(film.getLikes());
        assertEquals("ВТОРОЙ ФИЛЬМ", film.getName(), "Name изменилось");
        assertEquals("ЛЕГЕНДАРНОЕ ПРОДОЛЖЕНИЕ", film.getDescription(), "Description изменилось");
        assertEquals(90, film.getDuration(), "Duration изменилось");
        assertEquals(LocalDate.of(2010, 1, 1), film.getReleaseDate(), "ReleaseDate изменилась");
        assertEquals(new Rating((long) 5, "NC-17"), film.getMpa(), "Rating изменилось");
        assertEquals(3, film.getGenres().size(), "Genres изменилось");

        List<User> allUsers = userService.getAllUsers();
        assertEquals(4, allUsers.size(), "Количество пользователей изменилось");

        List<Film> allFilms = filmService.getAllFilms();
        assertEquals(2, allFilms.size(), "Количество пользователей изменилось");


        assertThrows(ValidationException.class, () -> userService.findUserById(9999));
        assertThrows(ValidationException.class, () -> filmService.findFilmById(-1));
    }

    @Test
    void ratingAlreadyShoudBeInDataBase() throws ValidationException {
        String ratingName = filmService.findRatingById(2).getName();
        List<Rating> allRatings = filmService.getAllRatings();

        assertEquals("PG", ratingName, "Rating name изменилось");
        assertEquals(5, allRatings.size(), "Количество rating изменилось");
        assertThrows(ValidationException.class, () -> filmService.findRatingById(-1));
    }

    @Test
    void genresAlreadyShoudBeInDataBase() throws ValidationException {
        String genreName = filmService.findGenreById(6).getName();
        List<Genre> allGenre = filmService.getAllGenres();

        assertEquals("Боевик", genreName, "Genre name изменилось");
        assertEquals(6, allGenre.size(), "Количество rating изменилось");
        assertThrows(ValidationException.class, () -> filmService.findGenreById(-1));
    }

    @Test
    void findUserFriendsHasNoFriend() throws ValidationException {
        List<User> friends = userService.findUserFriends(1);
        assertEquals(0, friends.size(), "FRIENDS изменилось");
        assertThrows(ValidationException.class, () -> userService.findUserFriends(-1));
    }

    @Test
    void userFriendsOperations() throws ValidationException {
        userService.addUserFriend(2, 3);
        userService.addUserFriend(2, 4);
        userService.addUserFriend(3, 4);

        int thirdUserFriendsCount = userService.findUserFriends(3).size();
        assertEquals(1, thirdUserFriendsCount, "FRIENDS изменилось");

        int numberOfCommonFriends = userService.findUsersCommonFriends(2, 4).size();
        assertEquals(0, numberOfCommonFriends, "COMMON FRIENDS wrong");

        numberOfCommonFriends = userService.findUsersCommonFriends(2, 3).size();
        long commonFriendId = userService.findUsersCommonFriends(2, 3).get(0).getId();
        assertEquals(1, numberOfCommonFriends, "COMMON FRIENDS wrong");
        assertEquals(4, commonFriendId, "COMMON FRIEND ID wrong");

        userService.deleteUserFriend(2L, 3L);
        int secondUserFriendsCountAfterDelete = userService.findUserFriends(2).size();
        assertEquals(1, secondUserFriendsCountAfterDelete, "FRIENDS изменилось");

        userService.deleteUserFriend(2L, 3L);
        userService.deleteUserFriend(2L, 4L);
        userService.deleteUserFriend(3L, 4L);
    }

    @Test
    void filmLikesOperations() throws ValidationException {
        filmService.addFilmLike(1, 4);
        filmService.addFilmLike(2, 2);
        filmService.addFilmLike(2, 3);
        filmService.addFilmLike(2, 1);

        Set<Long> firstFilmLikes = filmService.findFilmById(1).getLikes();
        assertEquals(1, firstFilmLikes.size(), "Count of LIKES wrong");
        assertTrue(firstFilmLikes.contains((long) 4), "LIKED USER ID wrong");

        List<Film> expectedPopularFilmList = List.of(filmService.findFilmById(2), filmService.findFilmById(1));
        List<Film> popularFilmList = filmService.findPopularFilms(0);
        assertEquals(expectedPopularFilmList, popularFilmList, "Popular films wrong");

        filmService.deleteFilmLike(2, 3);
        Set<Long> secondFilmLikes = filmService.findFilmById(2).getLikes();
        assertEquals(2, secondFilmLikes.size(), "Count of LIKES wrong");
        assertTrue(secondFilmLikes.contains((long) 1) && secondFilmLikes.contains((long) 2), "LIKED USER ID wrong");

        filmService.deleteFilmLike(1, 4);
        filmService.deleteFilmLike(2, 2);
        filmService.deleteFilmLike(2, 3);
        filmService.deleteFilmLike(2, 1);
    }

    @Test
    void updateUsersAndFilms() throws ValidationException {
        Set<Long> friendsOfUpdatedUser = new HashSet<>();
        friendsOfUpdatedUser.add((long) 2);
        User updatedUser = new User((long) 1, "UPDATED@yandex.ru", "UPDATED_GymBoss", "UPDATED_Van",
                LocalDate.of(1940, 10, 24), friendsOfUpdatedUser);

        userService.updateUser(updatedUser);
        User calledUserAfterUpdated = userService.findUserById(1);
        assertEquals(updatedUser.getId(), calledUserAfterUpdated.getId(), "ID NOT UPDATED");
        assertEquals(updatedUser.getEmail(), calledUserAfterUpdated.getEmail(), "EMAIL NOT UPDATED");
        assertEquals(updatedUser.getLogin(), calledUserAfterUpdated.getLogin(), "LOGIN NOT UPDATED");
        assertEquals(updatedUser.getName(), calledUserAfterUpdated.getName(), "NAME NOT UPDATED");
        assertEquals(updatedUser.getBirthday(), calledUserAfterUpdated.getBirthday(), "BIRTHDATE NOT UPDATED");
        assertEquals(updatedUser.getFriends(), calledUserAfterUpdated.getFriends(), "FRIENDS NOT UPDATED");

        TreeSet<Genre> updatedFilmGenres = new TreeSet<>();
        updatedFilmGenres.add(new Genre((long) 2, "Драма"));
        Rating updatedFilmRating = new Rating((long) 4, "R");
        Set<Long> updatedFilmLikes = new HashSet<>();
        updatedFilmLikes.add((long) 3);
        Film updatedFilm = new Film((long) 1, "UPDATED_ПЕРВЫЙ ФИЛЬМ", "UPDATED_ОПИСАНИЕ МЕНЬШЕ 200 СИМВОЛОВ",
                120, LocalDate.of(1900, 1, 1), updatedFilmLikes, updatedFilmRating,
                updatedFilmGenres);
        filmService.updateFilm(updatedFilm);
        Film calledFilmAfterUpdated = filmService.findFilmById(1);
        assertEquals(updatedFilm, calledFilmAfterUpdated, "FILM NOT UPDATED");
    }

    @Test
    @BeforeEach
    void createFilmsAndUsers() {
        embeddedDatabase = new EmbeddedDatabaseBuilder()
                .addScript("schema.sql")
                .addScript("data.sql")
                .setType(EmbeddedDatabaseType.H2)
                .build();
        jdbcTemplate = new JdbcTemplate(embeddedDatabase);
        userService = new UserService(new UserDbStorage(new UserDaoImpl(jdbcTemplate)));
        filmService = new FilmService(new FilmDbStorage(new FilmDaoImpl(jdbcTemplate)), new RatingDaoImpl(jdbcTemplate),
                new GenreDaoImpl(jdbcTemplate));

        TreeSet<Genre> firstFilmGenres = new TreeSet<>();
        firstFilmGenres.add(new Genre((long) 1, "Комедия"));
        Rating firstFilmRating = new Rating((long) 1, "G");
        Film film1 = new Film(null, "ПЕРВЫЙ ФИЛЬМ", "ОПИСАНИЕ МЕНЬШЕ 200 СИМВОЛОВ", 60,
                LocalDate.of(2000, 1, 1), null, firstFilmRating, firstFilmGenres);

        TreeSet<Genre> secondFilmGenres = new TreeSet<>();
        secondFilmGenres.add(new Genre((long) 1, "Комедия"));
        secondFilmGenres.add(new Genre((long) 4, "Триллер"));
        secondFilmGenres.add(new Genre((long) 5, "Документальный"));
        Rating secondFilmRating = new Rating((long) 5, "NC-17");
        Film film2 = new Film(null, "ВТОРОЙ ФИЛЬМ", "ЛЕГЕНДАРНОЕ ПРОДОЛЖЕНИЕ", 90,
                LocalDate.of(2010, 1, 1), null, secondFilmRating, secondFilmGenres);

        User user1 = new User(null, "first@yandex.ru", "GymBoss", "Van",
                LocalDate.of(1950, 10, 24), null);
        User user2 = new User(null, "second@yandex.ru", "Boy", "Billy",
                LocalDate.of(1960, 10, 24), null);
        User user3 = new User(null, "third@yandex.ru", "Next", "Ricardo",
                LocalDate.of(1970, 10, 24), null);
        User user4 = new User(null, "fourth@yandex.ru", "Door", "Nurminskiy",
                LocalDate.of(1980, 10, 24), null);

        filmService.addFilm(film1);
        filmService.addFilm(film2);
        userService.addNewUser(user1);
        userService.addNewUser(user2);
        userService.addNewUser(user3);
        userService.addNewUser(user4);
    }

    @Test
    @AfterEach
    void shutDown() {
        embeddedDatabase.shutdown();
    }
}