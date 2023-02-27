package ru.yandex.practicum.filmorate.storage;

import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.User;

import java.util.Map;

public interface UserStorage {
    void addUser(User user);
    void updateUser(User user) throws ValidationException;
    User findUserById(long id) throws ValidationException;
    Map<Long, User> getAllUsers();
}
