package ru.yandex.practicum.filmorate.storage;

import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.dao.UserDao;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.User;

import java.util.List;

@Component
@Primary
@RequiredArgsConstructor
public class UserDbStorage implements UserStorage {
    private final UserDao userDao;

    @Override
    public Long addUser(User user) {
        return userDao.createUser(user);
    }

    @Override
    public boolean updateUser(User user) {
        return userDao.updateUser(user);
    }

    @Override
    public User findUserById(long id) throws ValidationException {
        return userDao.findUserById(id).orElseThrow(() ->
                new ValidationException("Incorrect ID=" + id + ". This user is not in database yet"));
    }

    @Override
    public List<User> getAllUsers() {
        return userDao.findAllUsers();
    }
}