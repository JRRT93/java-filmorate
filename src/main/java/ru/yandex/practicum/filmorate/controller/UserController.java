package ru.yandex.practicum.filmorate.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.dao.InMemoryUserStorage;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.service.UserValidationService;

import javax.validation.Valid;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@RestController
@Slf4j
@Validated
public class UserController {
    private final InMemoryUserStorage inMemoryUserStorage = new InMemoryUserStorage();
    private final UserValidationService userValidationService = new UserValidationService();

    /**
     * Метод создает и возвращает ArrayList, в который складывает User из TreeMap в порядке возрастания ID.
     */
    @GetMapping("/users")
    public List<User> getAllUsers() {
        log.info("GET request for /users path received");
        List<User> listOfUsers = new ArrayList<>();
        for (Map.Entry<Integer, User> entry : inMemoryUserStorage.getUsers().entrySet()) {
            listOfUsers.add(entry.getValue());
        }
        log.debug("List of users provided from TreeMap");
        return listOfUsers;
    }

    /**
     * Film из запроса проходит валидацию полей нотациями String, при неудаче ответ клиенту с кодом 400. При успехе
     * метод вызывает валидацию оставшихся полей методом validateUser класса UserValidationService. При неудачной
     * валидации выбрасывается кастомное исключение ValidationException и ответ клиенту с кодом 500. При успехе вызывается
     * метод addUser класса InMemoryUserStorage и клиенту возвращается добавленный user.
     */
    @PostMapping("/users")
    public User addNewUser (@RequestBody @Valid User user) throws ValidationException {
        log.info("POST request for /users path received");
        userValidationService.validateUser(user);
        log.debug("All fields for USER validated successful");
        user = inMemoryUserStorage.addUser(user);
        log.debug("USER successful added. ID=" + user.getId() + ", name initialized as " + user.getName());
        return user; //todo проверить добавляются ли косячные запросы в мапу и убрать
    }

    /**
     * User из запроса проходит валидацию полей нотациями String, при неудаче ответ клиенту с кодом 400. При успехе
     * метод вызывает валидацию оставшихся полей методом validateUser класса UserValidationService. При неудачной
     * валидации выбрасывается кастомное исключение ValidationException и ответ клиенту с кодом 500. При успехе вызывается
     * метод updateUser класса InMemoryUserStorage и клиенту возвращается обновленный user.
     */
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
