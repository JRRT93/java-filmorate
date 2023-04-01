package ru.yandex.practicum.filmorate.dao;

import lombok.RequiredArgsConstructor;
import org.springframework.dao.EmptyResultDataAccessException;
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
import java.util.*;

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
            .likes(new TreeSet<>())
            .mpa(null)
            .genres(new TreeSet<>())
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
    public Optional<Film> findFilmById(long id) throws ValidationException {
        String sqlQuery = "SELECT * FROM films WHERE film_id = ?;";
        try {
            Film film = jdbcTemplate.queryForObject(sqlQuery, rowMapper, id);
            film.setGenres(new TreeSet<>(extractGenres(film)));
            film.setLikes(new HashSet<>(extractLikes(film)));
            film.setMpa(extractRating(film));
            return Optional.of(film);
        } catch (EmptyResultDataAccessException e) {
            return Optional.empty();
        }
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
        String sqlQuery = "SELECT  fg.genre_id, g.name\n" +
                "FROM film_genre as fg\n" +
                "INNER JOIN genres AS g ON fg.genre_id = g.genre_id\n" +
                "WHERE fg.film_id = ?" +
                "ORDER BY fg.genre_id ASC;";
        return jdbcTemplate.query(sqlQuery, (rs, rowNum) -> new Genre(rs.getLong("genre_id"),
                rs.getString("name")), film.getId());
    }

    private Rating extractRating(Film film) {
        String sqlQuery = "SELECT f.rating_mpa, r.name\n" +
                "FROM films AS f\n" +
                "INNER JOIN ratings AS r ON f.rating_mpa = r.rating_id\n" +
                "WHERE f.film_id = ?;";
        return jdbcTemplate.queryForObject(sqlQuery, (rs, rowNum) -> new Rating(rs.getLong("rating_mpa"),
                rs.getString("name")), film.getId());
    }

    private List<Long> extractLikes(Film film) {
        String sqlQuery = "SELECT user_id FROM film_likes WHERE film_id = ?\n" +
                "ORDER BY user_id ASC;";
        return jdbcTemplate.queryForList(sqlQuery, Long.class, film.getId());
    }
}