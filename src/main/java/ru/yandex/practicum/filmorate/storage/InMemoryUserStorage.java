package ru.yandex.practicum.filmorate.storage;

import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.User;

import java.util.*;

@Slf4j
@NoArgsConstructor
@Component
public class InMemoryUserStorage implements UserStorage {
    private final Map<Long, User> users = new TreeMap<>();

    public void addUser(User user) {
        if(user.getFriends() == null) user.setFriends(new TreeSet<>());
        log.debug("FRIENDS field initialized");
        users.put(user.getId(), user);
    }

    public void updateUser(User updatedUser) throws ValidationException {
        long updatedUserID = updatedUser.getId();
        checkUserInStorage(updatedUserID);
        if(updatedUser.getFriends() == null) updatedUser.setFriends(users.get(updatedUserID).getFriends());
        log.debug("FRIENDS field initialized");
        users.replace(updatedUserID, updatedUser);
    }

    public User findUserByID(long id) throws ValidationException {
        checkUserInStorage(id);
        return users.get(id);
    }

    public Map<Long, User> getAllUsers() {
        return users;
    }

    private void checkUserInStorage(long id) throws ValidationException {
        if (users.get(id) == null) {
            throw new ValidationException("Incorrect ID=" + id + ". This user is not in database yet");
        }
    }
}
