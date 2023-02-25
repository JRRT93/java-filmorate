package ru.yandex.practicum.filmorate.dao;

import lombok.Getter;
import lombok.NoArgsConstructor;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.User;

import java.util.List;
import java.util.Map;
import java.util.TreeMap;


@Getter
@NoArgsConstructor
public class InMemoryUserStorage {
    private final Map<Integer, User> users = new TreeMap<>();
    private int id = 1;

    public User addUser(User user) {
        user = checkUsersName(user);
        user.setId(id);
        users.put(id, user);
        ++id;
        return user;
    }
    public User updateUser(User user) throws ValidationException {
        user = checkUsersName(user);
        if (users.get(user.getId()) != null) {
            users.replace(user.getId(), user);
            return user;
        } else {
            throw new ValidationException("Incorrect ID. This user is not in database yet");
        }
    }

    public List<User> getAllUsers() {
        return List.copyOf(users.values());
    }

    private User checkUsersName (User user) {
        if (user.getName() == null || user.getName().isBlank()) {
            user.setName(user.getLogin());
        }
        return user;
    }
}
