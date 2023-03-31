package ru.yandex.practicum.filmorate.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.service.UserService;

import javax.validation.Valid;
import java.util.List;

@RestController
@Slf4j
@Validated
@RequiredArgsConstructor
public class UserController {
    private final UserService userService;

    @GetMapping("/users")
    public List<User> getAllUsers() {
        log.info("GET request for /users path received");
        return userService.getAllUsers();
    }

    @PostMapping("/users")
    public User createUser(@RequestBody @Valid User user) {
        log.info("POST request for /users path received");
        log.debug("USER required fields for autovalidation validated successful");
        return userService.addNewUser(user);
    }

    @PutMapping("/users")
    public User updateUser(@RequestBody @Valid User user) throws ValidationException {
        log.info("PUT request for /users path received");
        log.debug("USER required fields for autovalidation validated successful");
        userService.updateUser(user);
        return user;
    }

    @GetMapping("/users/{id}")
    public User findUserById(@PathVariable long id) throws ValidationException {
        log.info("GET request for /users/" + id + " path received");
        return userService.findUserById(id);
    }

    @PutMapping(value = "/users/{id}/friends/{friendId}")
    public void addUserFriend(@PathVariable long id, @PathVariable long friendId) throws ValidationException {
        log.info("PUT request for /users/" + id + "/friends/" + friendId + " path received");
        userService.addUserFriend(id, friendId);
    }

    @DeleteMapping(value = "/users/{id}/friends/{friendId}")
    public void deleteUserFriend(@PathVariable long id, @PathVariable long friendId) throws ValidationException {
        log.info("DEL request for /users/" + id + "/friends/" + friendId + " path received");
        userService.deleteUserFriend(id, friendId);
    }

    @GetMapping(value = "/users/{id}/friends")
    public List<User> findUserFriends(@PathVariable long id) throws ValidationException {
        log.info("GET request for /users/" + id + "/friends path received");
        return userService.findUserFriends(id);
    }

    @GetMapping(value = "/users/{id}/friends/common/{otherId}")
    public List<User> findUserCommonFriends(@PathVariable long id, @PathVariable(name = "otherId") long friendId)
            throws ValidationException {
        log.info("GET request for /users/" + id + "/friends/common" + friendId + " path received");
        return userService.findUsersCommonFriends(id, friendId);
    }
}