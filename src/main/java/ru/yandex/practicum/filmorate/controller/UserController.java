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
    public User createUser (@RequestBody @Valid User user) {
        log.info("POST request for /users path received");
        log.debug("All fields for USER validated successful");
        return userService.addNewUser(user);
    }

    @PutMapping("/users")
    public User updateUser (@RequestBody @Valid User user) throws ValidationException {
        log.info("PUT request for /users path received");
        userService.updateUser(user);
        log.debug("All fields for USER validated successful");
        return user;
    }

    @GetMapping("/users/{id}")
    public User findUserByID(@PathVariable long id) throws ValidationException {
        return userService.findUserByID(id);
    }

    @PutMapping(value = "/users/{id}/friends/{friendId}")
    public void addUserFriend(@PathVariable long id, @PathVariable long friendId) throws ValidationException {
        userService.addUserFriend(id, friendId);
    }

    @DeleteMapping(value = "/users/{id}/friends/{friendId}")
    public void deleteUserFriend(@PathVariable long id, @PathVariable long friendId) throws ValidationException {
        userService.deleteUserFriend(id, friendId);
    }

    @GetMapping(value = "/users/{id}/friends")
    public List<User> findUserFriends(@PathVariable long id) throws ValidationException {
        return userService.findUserFriends(id);
    }

    @GetMapping(value = "/users/{id}/friends/common/{otherId}")
    public List<User> findUserCommonFriends(@PathVariable long id, @PathVariable (name = "otherId") long friendId)
            throws ValidationException {
        return userService.findUsersCommonFriends(id, friendId);
    }
}
