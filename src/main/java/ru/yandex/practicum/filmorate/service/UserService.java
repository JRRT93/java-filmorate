package ru.yandex.practicum.filmorate.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.UserStorage;

import java.util.ArrayList;
import java.util.List;
import java.util.TreeSet;

@Slf4j
@Service
@RequiredArgsConstructor
public class UserService {
    private final UserStorage userStorage;

    public User addNewUser(User user) {
        checkUsersName(user);
        if (user.getFriends() == null) user.setFriends(new TreeSet<>());
        log.debug("FRIENDS field initialized");
        user.setId(userStorage.addUser(user));
        log.debug("USER successful added. ID=" + user.getId());
        return user;
    }

    public User updateUser(User user) throws ValidationException {
        userStorage.findUserById(user.getId());
        checkUsersName(user);
        if (user.getFriends() == null) user.setFriends(new TreeSet<>());
        log.debug("FRIENDS field initialized");
        if (userStorage.updateUser(user)) {
            log.debug("USER successful updated. ID=" + user.getId());
        }
        return user;
    }

    public List<User> getAllUsers() {
        return userStorage.getAllUsers();
    }

    public User findUserById(long id) throws ValidationException {
        return userStorage.findUserById(id);
    }

    public void addUserFriend(long id, long friendId) throws ValidationException {
        User user = userStorage.findUserById(id);
        userStorage.findUserById(friendId);
        user.getFriends().add(friendId);
        if (userStorage.updateUser(user)) {
            log.debug("FRIEND ID=" + friendId + " added from User ID=" + id + " friends list");
        }
    }

    public void deleteUserFriend(Long id, Long friendId) throws ValidationException {
        User user = userStorage.findUserById(id);
        userStorage.findUserById(friendId);
        user.getFriends().remove(friendId);
        if (userStorage.updateUser(user)) {
            log.debug("FRIEND ID=" + friendId + " removed from User ID=" + id + " friends list");
        }
    }

    public List<User> findUserFriends(long id) throws ValidationException {
        List<User> friendsList = new ArrayList<>();
        for (Long userId : userStorage.findUserById(id).getFriends()) {
            friendsList.add(userStorage.findUserById(userId));
        }
        return friendsList;
    }

    public List<User> findUsersCommonFriends(long id, long friendId) throws ValidationException {
        List<User> commonFriends = new ArrayList<>();
        List<Long> userFriends = new ArrayList<>(userStorage.findUserById(id).getFriends());
        List<Long> friendFriends = new ArrayList<>(userStorage.findUserById(friendId).getFriends());
        for (Long userFriend : userFriends) {
            if (friendFriends.contains(userFriend)) commonFriends.add(this.findUserById(userFriend));
        }
        return commonFriends;
    }

    private void checkUsersName(User user) {
        if (user.getName() == null || user.getName().isBlank()) {
            user.setName(user.getLogin());
            log.debug("NAME field initialized as " + user.getName());
        }
    }
}