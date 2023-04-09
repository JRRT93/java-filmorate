package ru.yandex.practicum.filmorate.storage;

import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.User;

import java.util.List;

public interface UserStorage {
    Long addUser(User user);

    boolean updateUser(User user) throws ValidationException;

    User findUserById(long id) throws ValidationException;

    List<User> getAllUsers();
}