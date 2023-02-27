package ru.yandex.practicum.filmorate.storage;

import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.User;

import java.util.Map;
import java.util.TreeMap;
import java.util.TreeSet;

class InMemoryUserStorageTest {
    private final Map<Long, User> users = new TreeMap<>();

    public void addUser(User user) {
        users.put(user.getId(), user);
    }

    public void updateUser(User updatedUser) throws ValidationException {
        long updatedUserID = updatedUser.getId();
        if (users.get(updatedUserID) == null) {
            throw new ValidationException("Incorrect ID=" + updatedUserID + ". This user is not in database yet");
        }
        users.replace(updatedUserID, updatedUser);
    }

    public User findUserByID(long id) throws ValidationException {
        if (users.get(id) == null) {
            throw new ValidationException("Incorrect ID=" + id + ". This user is not in database yet");
        }
        return users.get(id);
    }

    public Map<Long, User> getAllUsers() {
        return users;
    }
}