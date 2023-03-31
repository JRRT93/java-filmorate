package ru.yandex.practicum.filmorate.dao;

import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.User;

import java.util.List;

public interface UserDao {
    Long createUser(User user);

    boolean updateUser(User user);

    User findUserById(long id) throws ValidationException;

    List<User> findAllUsers();
}
