package ru.yandex.practicum.filmorate.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.dao.InMemoryUserStorage;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.service.UserValidationService;

import javax.validation.Valid;
import java.util.List;

@RestController
@Slf4j
@Validated
public class UserController {
    private final InMemoryUserStorage inMemoryUserStorage = new InMemoryUserStorage();
    private final UserValidationService userValidationService = new UserValidationService();

    @GetMapping("/users")
    public List<User> getAllUsers() {
        log.info("GET request for /users path received");
        return inMemoryUserStorage.getAllUsers();
    }

    @PostMapping("/users")
    public User addNewUser (@RequestBody @Valid User user) throws ValidationException {
        log.info("POST request for /users path received");
        userValidationService.validateUser(user);
        log.debug("All fields for USER validated successful");
        user = inMemoryUserStorage.addUser(user);
        log.debug("USER successful added. ID=" + user.getId() + ", name initialized as " + user.getName());
        return user;
    }

    @PutMapping("/users")
    public User updateUser (@RequestBody @Valid User user) throws ValidationException {
        log.info("PUT request for /users path received");
        userValidationService.validateUser(user);
        log.debug("All fields for USER validated successful");
        user = inMemoryUserStorage.updateUser(user);
        log.debug("USER successful added. ID=" + user.getId() + ", name initialized as " + user.getName());
        return user;
    }
}
