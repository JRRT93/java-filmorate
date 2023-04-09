package ru.yandex.practicum.filmorate.storage;

import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.User;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

class InMemoryUserStorageTest {
    private final Map<Long, User> users = new TreeMap<>();
    private long id = 1;

    public Long addUser(User user) {
        user.setId(id);
        id++;
        users.put(user.getId(), user);
        return user.getId();
    }

    public boolean updateUser(User updatedUser) throws ValidationException {
        long updatedUserID = updatedUser.getId();
        User oldValueUser = users.get(updatedUserID);
        if (oldValueUser == null) {
            throw new ValidationException("Incorrect ID=" + updatedUserID + ". This user is not in database yet");
        }
        return users.replace(updatedUserID, oldValueUser, updatedUser);
    }

    public User findUserById(long id) throws ValidationException {
        if (users.get(id) == null) {
            throw new ValidationException("Incorrect ID=" + id + ". This user is not in database yet");
        }
        return users.get(id);
    }

    public List<User> getAllUsers() {
        return new ArrayList<>(users.values());
    }
}