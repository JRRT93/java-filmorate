package ru.yandex.practicum.filmorate.dao;

import lombok.RequiredArgsConstructor;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.model.Genre;

import java.util.List;
import java.util.Optional;

@Component
@RequiredArgsConstructor
public class GenreDaoImpl implements GenreDao {
    private final JdbcTemplate jdbcTemplate;
    private final RowMapper<Genre> rowMapper = (rs, rowNum) -> new Genre(rs.getLong("genre_id"),
            rs.getString("name"));

    @Override
    public List<Genre> getAllGenres() {
        String sqlQuery = "SELECT * FROM genres";
        return jdbcTemplate.query(sqlQuery, rowMapper);
    }

    @Override
    public Optional<Genre> findGenreById(long id) {
        String sqlQuery = "SELECT * FROM genres WHERE genre_id = ?;";
        try {
            return Optional.ofNullable(jdbcTemplate.queryForObject(sqlQuery, rowMapper, id));
        } catch (EmptyResultDataAccessException e) {
            return Optional.empty();
        }
    }
}
