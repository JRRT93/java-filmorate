package ru.yandex.practicum.filmorate.service;

import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.InMemoryUserStorage;
import ru.yandex.practicum.filmorate.storage.UserStorage;

import javax.validation.ConstraintViolation;
import javax.validation.Validation;
import javax.validation.Validator;
import javax.validation.ValidatorFactory;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;

import static org.junit.jupiter.api.Assertions.*;

@Slf4j
class UserServiceTest {
    private final UserStorage userStorage = new InMemoryUserStorage();
    private long id = 1;
    User user1;
    private static final Validator validator;
    static {
        ValidatorFactory validatorFactory = Validation.buildDefaultValidatorFactory();
        validator = validatorFactory.usingContext().getValidator();
    }

    @BeforeEach
    void makeUser() {
        user1 = new User(null, "zahodim@yandex.ru", "GymBoss", "Van",
                LocalDate.of(1972, 10, 24), null);

    }

    @Test
    void emailValidationShouldFindViolations() {
        Set<ConstraintViolation<User>> violations = validator.validate(user1);
        assertEquals(0, violations.size(), "Валидация zahodim@yandex.ru 100 не прошла");

        user1.setEmail(" ");
        violations = validator.validate(user1);
        assertEquals(2, violations.size(), "Валидация пустого емейла прошла");

        user1.setEmail("кривособаков@");
        violations = validator.validate(user1);
        assertEquals(1, violations.size(), "Валидация пустого некорректного емейла прошла");
    }

    @Test
    void loginValidationShouldFindViolations() {
        Set<ConstraintViolation<User>> violations = validator.validate(user1);
        assertEquals(0, violations.size(), "Валидация login GymBoss не прошла");

        user1.setLogin("Мохнатый Шмель");
        violations = validator.validate(user1);
        assertEquals(1, violations.size(), "Валидация login с пробелом прошла");

        user1.setLogin("");
        violations = validator.validate(user1);
        assertEquals(1, violations.size(), "Валидация пустого login прошла");
    }

    @Test
    void birthdayValidationShouldFindViolations() {
        Set<ConstraintViolation<User>> violations = validator.validate(user1);
        assertEquals(0, violations.size(), "Валидация 24.10.1972 не прошла");

        user1.setBirthday(LocalDate.of(2072, 10, 24));
        violations = validator.validate(user1);
        assertEquals(1, violations.size(), "Валидация 24.10.2072 прошла");
    }

    @Test
    void validateUserEmptyNameTest() {
        user1.setName(" ");
        addNewUser(user1);
        assertEquals("GymBoss", user1.getName(), "Name не было присвоено");
    }


    @Test
    void addUserIDShouldAssignedLikesInitialized() throws ValidationException {
        this.addNewUser(user1);
        assertNotNull(this.findUserById(1).getId());
        assertNotNull(this.findUserById(1).getFriends());
        assertEquals("Van", this.findUserById(1).getName(),"Name изменилось");
        assertEquals("GymBoss", this.findUserById(1).getLogin(),"Login изменилось");
        assertEquals("zahodim@yandex.ru", this.findUserById(1).getEmail(),"Email изменилось");
        assertEquals(LocalDate.of(1972, 10, 24), this.findUserById(1).getBirthday(),
                "Birthday изменилась");
    }

    @Test
    void updateUserShouldUpdateAllInitializedFields () throws ValidationException {
        User updatedUser = new User((long) 1, "svorachivaemsya@yandex.ru", "Billy", "Herrington",
                LocalDate.of(1942, 10, 24), null);
        addNewUser(user1);
        updateUser(updatedUser);
        assertNotNull(this.findUserById(1).getFriends());
        assertEquals(1,this.findUserById(1).getId(), "ID изменилось");
        assertEquals("Herrington", this.findUserById(1).getName(),"Name не изменилось");
        assertEquals("Billy", this.findUserById(1).getLogin(),"Login не изменилось");
        assertEquals("svorachivaemsya@yandex.ru", this.findUserById(1).getEmail(),"Email не изменилось");
        assertEquals(LocalDate.of(1942, 10, 24), this.findUserById(1).getBirthday(),
                "Birthday не изменилась");
    }

    @Test
    void updateFilmShouldThrowExpDueInvalidID () {
        User updatedUser = new User((long) 999, "svorachivaemsya@yandex.ru", "Billy", "Herrington",
                LocalDate.of(1942, 10, 24), null);
        addNewUser(user1);
        assertThrows(ValidationException.class, () -> this.updateUser(updatedUser));
    }

    @Test
    void updateFilmShouldKeepInitialized () throws ValidationException {
        Set<Long> testSet = new TreeSet<>();
        testSet.add((long)14);
        User updatedUser =  new User((long) 1, "svorachivaemsya@yandex.ru", "Billy", "Herrington",
                LocalDate.of(1942, 10, 24), testSet);
        addNewUser(user1);
        updateUser(updatedUser);
        assertEquals(testSet,this.findUserById(1).getFriends(), "FRIENDS изменилось");
    }

    @Test
    void findUserFriendsHasNoFriend() throws ValidationException {
        Set<Long> friends = new TreeSet<>();
        user1.setFriends(friends);
        addNewUser(user1);
        assertEquals(0, this.findUserFriends(user1.getId()).size(), "FRIENDS изменилось");
    }

    @Test
    void findUserFriendsHasSomeFriend() throws ValidationException {
        User user2 = new User(null, "second@yandex.ru", "Nagibator69", "Petya",
                LocalDate.of(1993, 10, 24), null);
        addNewUser(user1);
        addNewUser(user2);
        addUserFriend(1,2);
        assertEquals(1, this.findUserFriends(user1.getId()).size(), "FRIENDS изменилось");
    }

    @Test
    void findUserCommonFriendsShouldFindZero() throws ValidationException {
        User user2 = new User(null, "second@yandex.ru", "Nagibator69", "Petya",
                LocalDate.of(1993, 10, 24), null);
        User user3 = new User(null, "third@yandex.ru", "DEAD-INSIDE2009", "Kolya",
                LocalDate.of(2009, 10, 24), null);
        addNewUser(user1);
        addNewUser(user2);
        addNewUser(user3);
        addUserFriend(2,3);
        assertEquals(0, this.findUsersCommonFriends(user1.getId(), user2.getId()).size(), "FRIENDS изменилось");
    }

    @Test
    void findUserCommonFriendsShouldFindOne() throws ValidationException {
        User user2 = new User(null, "second@yandex.ru", "Nagibator69", "Petya",
                LocalDate.of(1993, 10, 24), null);
        User user3 = new User(null, "third@yandex.ru", "DEAD-INSIDE2009", "Kolya",
                LocalDate.of(2009, 10, 24), null);
        addNewUser(user1);
        addNewUser(user2);
        addNewUser(user3);
        addUserFriend(1,2);
        addUserFriend(1,3);
        addUserFriend(2,3);
        assertEquals(1, this.findUsersCommonFriends(user1.getId(), user2.getId()).size(), "FRIENDS изменилось");
    }

    public User addNewUser(User user) {
        checkUsersName(user);
        user.setId(id);
        id++;
        if(user.getFriends() == null) user.setFriends(new TreeSet<>());
        log.debug("FRIENDS field initialized");
        userStorage.addUser(user);
        log.debug("USER successful added. ID=" + user.getId());
        return user;
    }

    public User updateUser(User user) throws ValidationException {
        checkUsersName(user);
        if(user.getFriends() == null) user.setFriends(new TreeSet<>());
        log.debug("FRIENDS field initialized");
        userStorage.updateUser(user);
        log.debug("USER successful updated. ID=" + user.getId());
        return user;
    }

    public List<User> getAllUsers() {
        return new ArrayList<>(userStorage.getAllUsers().values());
    }

    public User findUserById(long id) throws ValidationException {
        return userStorage.findUserById(id);
    }

    public void addUserFriend (long id, long friendId) throws ValidationException {
        User user = userStorage.findUserById(id);
        User friend = userStorage.findUserById(friendId);
        user.getFriends().add(friendId);
        log.debug("FRIEND ID=" + friendId + " added to User ID=" + id + " friends list");
        friend.getFriends().add(id);
        log.debug("FRIEND ID=" + id + " added to User ID=" + friend + " friends list");
    }

    public void deleteUserFriend(Long id, Long friendId) throws ValidationException {
        User user = userStorage.findUserById(id);
        User friend = userStorage.findUserById(friendId);
        user.getFriends().remove(friendId);
        log.debug("FRIEND ID=" + friendId + " removed from User ID=" + id + " friends list");
        friend.getFriends().remove(id);
        log.debug("FRIEND ID=" + id + " removed from User ID=" + friend + " friends list");
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
        for (Long userFriend : userFriends)  {
            if (friendFriends.contains(userFriend)) commonFriends.add(this.findUserById(userFriend));
        }
        return commonFriends;
    }

    private void checkUsersName (User user) {
        if (user.getName() == null || user.getName().isBlank()) {
            user.setName(user.getLogin());
            log.debug("NAME field initialized as " + user.getName());
        }
    }
}