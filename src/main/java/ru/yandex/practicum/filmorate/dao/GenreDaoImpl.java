package ru.yandex.practicum.filmorate.dao;

import lombok.RequiredArgsConstructor;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.Genre;

import java.util.List;

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
    public Genre findGenreById(long id) throws ValidationException {
        String sqlQuery = "SELECT * FROM genres WHERE genre_id = ?;";
        Genre genre;
        try {
            genre = jdbcTemplate.queryForObject(sqlQuery, rowMapper, id);
        } catch (Exception e) {
            genre = null;
        }
        if (genre == null) throw new ValidationException("Incorrect ID=" + id + ". This genre is not in database yet");
        return genre;
    }
}
