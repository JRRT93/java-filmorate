package ru.yandex.practicum.filmorate.service;

import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.User;

public class UserValidationService {

    public void validateUser(User user) throws ValidationException {
        if (!user.getEmail().contains("@")) {
            throw new ValidationException("Email should contain @!");
        }
        if (user.getLogin().contains(" ")) {
            throw new ValidationException("Login should not contain spaces!");
        }
    }
}
