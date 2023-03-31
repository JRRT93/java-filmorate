package ru.yandex.practicum.filmorate.dao;

import lombok.RequiredArgsConstructor;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.Rating;

import java.util.List;

@Component
@RequiredArgsConstructor
public class RatingDaoImpl implements RatingDao {
    private final JdbcTemplate jdbcTemplate;
    private final RowMapper<Rating> rowMapper = (rs, rowNum) -> new Rating(rs.getLong("rating_id"),
            rs.getString("name"));

    @Override
    public List<Rating> getAllRatings() {
        String sqlQuery = "SELECT * FROM ratings";
        return jdbcTemplate.query(sqlQuery, rowMapper);
    }

    @Override
    public Rating findRatingById(long id) throws ValidationException {
        String sqlQuery = "SELECT * FROM ratings WHERE rating_id = ?;";
        Rating rating;
        try {
            rating = jdbcTemplate.queryForObject(sqlQuery, rowMapper, id);
        } catch (Exception e) {
            rating = null;
        }
        if (rating == null)
            throw new ValidationException("Incorrect ID=" + id + ". This rating is not in database yet");
        return rating;
    }
}
