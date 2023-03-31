package ru.yandex.practicum.filmorate.dao;

import lombok.RequiredArgsConstructor;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.model.Rating;

import java.sql.Date;
import java.sql.PreparedStatement;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;

@Component
@RequiredArgsConstructor
public class FilmDaoImpl implements FilmDao {
    private final JdbcTemplate jdbcTemplate;
    private final RowMapper<Film> rowMapper = (rs, rowNum) -> Film.builder()
            .id(rs.getLong("film_id"))
            .name(rs.getString("name"))
            .description(rs.getString("description"))
            .duration(rs.getInt("duration"))
            .releaseDate(rs.getDate("release_date").toLocalDate())
            .likes(null)
            .mpa(null)
            .genres(null)
            .build();

    @Override
    public Long createFilm(Film film) {
        String sqlQuery = "INSERT INTO films (name, description, duration, release_date, rating_mpa) " +
                "VALUES (?, ?, ?, ?, ?);";
        KeyHolder keyHolder = new GeneratedKeyHolder();
        boolean isCreated = jdbcTemplate.update(connection -> {
            PreparedStatement statement = connection.prepareStatement(sqlQuery, new String[]{"film_id"});
            statement.setString(1, film.getName());
            statement.setString(2, film.getDescription());
            statement.setInt(3, film.getDuration());
            statement.setDate(4, Date.valueOf(film.getReleaseDate()));
            statement.setLong(5, film.getMpa().getId());
            return statement;
        }, keyHolder) > 0;
        long generatedId = keyHolder.getKey().longValue();
        if (isCreated) {
            putFilmLikes(film, generatedId);
            putFilmGenres(film, generatedId);
        }
        return generatedId;
    }

    @Override
    public boolean updateFilm(Film film) {
        String sqlQuery = "UPDATE films SET name = ?, description = ?, duration = ?, release_date = ?, rating_mpa = ?" +
                " WHERE film_id = ?;";
        boolean isUpdated = jdbcTemplate.update(sqlQuery, film.getName(), film.getDescription(), film.getDuration(),
                film.getReleaseDate(), film.getMpa().getId(), film.getId()) > 0;
        if (isUpdated) {
            updateFilmLikes(film);
            updateFilmGenre(film);
        }
        return isUpdated;
    }

    @Override
    public Film findFilmById(long id) throws ValidationException {
        String sqlQuery = "SELECT * FROM films WHERE film_id = ?;";
        Film film;
        try {
            film = jdbcTemplate.queryForObject(sqlQuery, rowMapper, id);
        } catch (Exception e) {
            film = null;
        }
        if (film == null) throw new ValidationException("Incorrect ID=" + id + ". This film is not in database yet");
        film.setGenres(new TreeSet<>(extractGenres(film)));
        film.setLikes(new HashSet<>(extractLikes(film)));
        film.setMpa(extractRating(film));
        return film;
    }

    @Override
    public List<Film> findAllFilms() {
        String sqlQuery = "SELECT * FROM films";
        List<Film> allFilms = jdbcTemplate.query(sqlQuery, rowMapper);
        allFilms.forEach(film -> {
            film.setGenres(new TreeSet<>(extractGenres(film)));
            film.setLikes(new HashSet<>(extractLikes(film)));
            film.setMpa(extractRating(film));
        });
        return allFilms;
    }

    private void putFilmLikes(Film film, long generatedId) {
        String sqlQuery = "INSERT INTO film_likes VALUES (?, ?);";
        Set<Long> likes = film.getLikes();
        if (likes != null && !likes.isEmpty()) {
            likes.forEach(id -> jdbcTemplate.update(sqlQuery, generatedId, id));
        }
    }

    private void putFilmGenres(Film film, long generatedId) {
        String sqlQuery = "INSERT INTO film_genre VALUES (?, ?);";
        Set<Genre> genres = film.getGenres();
        if (genres != null && !genres.isEmpty()) {
            genres.forEach(genre -> jdbcTemplate.update(sqlQuery, generatedId, genre.getId()));
        }
    }

    private void updateFilmLikes(Film film) {
        String sqlQuery = "DELETE FROM film_likes WHERE film_id = ?;";
        jdbcTemplate.update(sqlQuery, film.getId());
        putFilmLikes(film, film.getId());
    }

    private void updateFilmGenre(Film film) {
        String sqlQuery = "DELETE FROM film_genre WHERE film_id = ?;";
        jdbcTemplate.update(sqlQuery, film.getId());
        putFilmGenres(film, film.getId());
    }

    private List<Genre> extractGenres(Film film) {
        String sqlQuery = "SELECT  f.genre_id, g.name\n" +
                "FROM (SELECT genre_id FROM film_genre WHERE film_id = ?) as f\n" +
                "INNER JOIN genres AS g ON f.genre_id = g.genre_id\n" +
                "ORDER BY f.genre_id ASC;";
        return jdbcTemplate.query(sqlQuery, (rs, rowNum) -> new Genre(rs.getLong("genre_id"),
                rs.getString("name")), film.getId());
    }

    private Rating extractRating(Film film) {
        String sqlQuery = "SELECT rating_id, name FROM ratings " +
                "WHERE rating_id = (SELECT rating_mpa FROM films WHERE film_id = ?);";
        return jdbcTemplate.queryForObject(sqlQuery, (rs, rowNum) -> new Rating(rs.getLong("rating_id"),
                rs.getString("name")), film.getId());
    }

    private List<Long> extractLikes(Film film) {
        String sqlQuery = "SELECT user_id FROM film_likes WHERE film_id = ?\n" +
                "ORDER BY user_id ASC;";
        return jdbcTemplate.queryForList(sqlQuery, Long.class, film.getId());
    }
}