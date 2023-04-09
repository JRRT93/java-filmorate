package ru.yandex.practicum.filmorate.dao;

import lombok.RequiredArgsConstructor;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.model.Rating;

import java.util.List;
import java.util.Optional;

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
    public Optional<Rating> findRatingById(long id) {
        String sqlQuery = "SELECT * FROM ratings WHERE rating_id = ?;";
        try {
            return Optional.ofNullable(jdbcTemplate.queryForObject(sqlQuery, rowMapper, id));
        } catch (EmptyResultDataAccessException e) {
            return Optional.empty();
        }
    }
}
