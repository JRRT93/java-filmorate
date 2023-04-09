package ru.yandex.practicum.filmorate.dao;

import lombok.RequiredArgsConstructor;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.User;

import java.sql.Date;
import java.sql.PreparedStatement;
import java.util.*;

@Component
@RequiredArgsConstructor
public class UserDaoImpl implements UserDao {
    private final JdbcTemplate jdbcTemplate;
    private final RowMapper<User> rowMapper = (rs, rowNum) -> User.builder()
            .id(rs.getLong("user_id"))
            .email(rs.getString("email"))
            .login(rs.getString("login"))
            .name(rs.getString("name"))
            .birthday(rs.getDate("birthdate").toLocalDate())
            .friends(new TreeSet<>())
            .build();

    @Override
    public Long createUser(User user) {
        String sqlQuery = "INSERT INTO users (email, login, name, birthdate) VALUES (?, ?, ?, ?);";
        KeyHolder keyHolder = new GeneratedKeyHolder();
        boolean isCreated = jdbcTemplate.update(connection -> {
            PreparedStatement statement = connection.prepareStatement(sqlQuery, new String[]{"user_id"});
            statement.setString(1, user.getEmail());
            statement.setString(2, user.getLogin());
            statement.setString(3, user.getName());
            statement.setDate(4, Date.valueOf(user.getBirthday()));
            return statement;
        }, keyHolder) > 0;
        long generatedId = keyHolder.getKey().longValue();
        if (isCreated) putUsersFriends(user, generatedId);
        return generatedId;
    }

    @Override
    public boolean updateUser(User user) {
        String sqlQuery = "UPDATE users SET email = ?, login = ?, name = ?, birthdate = ? WHERE user_id = ?;";
        boolean isUpdated = jdbcTemplate.update(sqlQuery, user.getEmail(), user.getLogin(), user.getName(), user.getBirthday(),
                user.getId()) > 0;
        if (isUpdated) updateUsersFriends(user);
        return isUpdated;
    }

    @Override
    public Optional<User> findUserById(long id) throws ValidationException {
        String sqlQuery = "SELECT * FROM users WHERE user_id = ?;";
        try {
            User user = jdbcTemplate.queryForObject(sqlQuery, rowMapper, id);
            user.setFriends(new HashSet<>(extractUsersFriends(user)));
            return Optional.of(user);
        } catch (EmptyResultDataAccessException e) {
            return Optional.empty();
        }
    }

    @Override
    public List<User> findAllUsers() {
        String sqlQuery = "SELECT * FROM users";
        List<User> allUsers = jdbcTemplate.query(sqlQuery, rowMapper);
        allUsers.forEach(user -> user.setFriends(new HashSet<>(extractUsersFriends(user))));
        return allUsers;
    }

    private void putUsersFriends(User user, long generatedId) {
        String sqlQuery = "INSERT INTO user_friends VALUES (?, ?);";
        Set<Long> friends = user.getFriends();
        if (friends != null && !friends.isEmpty()) {
            friends.forEach(id -> jdbcTemplate.update(sqlQuery, generatedId, id));
        }
    }

    private List<Long> extractUsersFriends(User user) {
        String sqlQuery = "SELECT friend_id FROM user_friends WHERE user_id = ?;";
        return jdbcTemplate.queryForList(sqlQuery, Long.class, user.getId());
    }

    private void updateUsersFriends(User user) {
        String sqlQuery = "DELETE FROM user_friends WHERE user_id = ?;";
        jdbcTemplate.update(sqlQuery, user.getId());
        putUsersFriends(user, user.getId());
    }
}