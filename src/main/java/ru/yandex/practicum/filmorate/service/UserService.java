package ru.yandex.practicum.filmorate.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.UserStorage;

import java.util.*;

@Slf4j
@Service
@RequiredArgsConstructor
public class UserService {
    private final UserStorage userStorage;
    private long id = 1;

    public User addNewUser(User user) {
        checkUsersName(user);
        user.setId(id);
        id++;
        userStorage.addUser(user);
        log.debug("USER successful added. ID=" + user.getId() + ", name initialized as " + user.getName());
        return user;
    }

    public User updateUser(User user) throws ValidationException {
        checkUsersName(user);
        userStorage.updateUser(user);
        log.debug("USER successful updated. ID=" + user.getId() + ", name initialized as " + user.getName());
        return user;
    }

    public List<User> getAllUsers() {
        return new ArrayList<>(userStorage.getAllUsers().values());
    }

    public User findUserByID(long id) throws ValidationException {
        return userStorage.findUserByID(id);
    }

    public void addUserFriend (long id, long friendId) throws ValidationException {
        User user = userStorage.findUserByID(id);
        User friend = userStorage.findUserByID(friendId);
        user.getFriends().add(friendId);
        friend.getFriends().add(id);
    }

    public void deleteUserFriend(Long id, Long friendId) throws ValidationException {
        User user = userStorage.findUserByID(id);
        User friend = userStorage.findUserByID(friendId);
        user.getFriends().remove(friendId);
        friend.getFriends().remove(id);
    }

    public List<User> findUserFriends(long id) throws ValidationException {
        List<User> friendsList = new ArrayList<>();
        for (Long userId : userStorage.findUserByID(id).getFriends()) {
            friendsList.add(userStorage.findUserByID(userId));
        }
        return friendsList;
    }

    public List<User> findUsersCommonFriends(long id, long friendId) throws ValidationException {
        List<User> commonFriends = new ArrayList<>();
        List<Long> userFriends = new ArrayList<>(userStorage.findUserByID(id).getFriends());
        List<Long> friendFriends = new ArrayList<>(userStorage.findUserByID(friendId).getFriends());
        for (Long userFriend : userFriends)  {
            if (friendFriends.contains(userFriend)) commonFriends.add(this.findUserByID(userFriend));
        }
        return commonFriends;
    }

    private void checkUsersName (User user) {
        if (user.getName() == null || user.getName().isBlank()) {
            user.setName(user.getLogin());
        }
    }
}
