package ru.yandex.practicum.filmorate.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.service.FilmService;

import javax.validation.Valid;
import javax.validation.constraints.Positive;
import java.util.List;

@RestController
@Slf4j
@Validated
@RequiredArgsConstructor
public class FilmController {
    private final FilmService filmService;
    private final UserController userController;

    @GetMapping("/films")
    public List<Film> getAllFilms() {
        log.info("GET request for /films path received");
        return filmService.getAllFilms();
    }

    @PostMapping("/films")
    public Film createFilm (@RequestBody @Valid Film film) {
        log.info("POST request for /films path received");
        log.debug("All fields for FILM validated successful");
        return filmService.addFilm(film);
    }

    @PutMapping("/films")
    public Film updateFilm (@RequestBody @Valid Film film) throws ValidationException {
        log.info("PUT request for /films path received");
        filmService.updateFilm(film);
        log.debug("All fields for FILM validated successful");
        return film;
    }

    @GetMapping("/films/{id}")
    public Film findFilmByID(@PathVariable long id) throws ValidationException {
        return filmService.findFilmByID(id);
    }

    @PutMapping("/films/{id}/like/{userId}")
    public void addFilmLike(@PathVariable long id, @PathVariable long userId) throws ValidationException {
        userController.findUserByID(userId);
        filmService.addFilmLike(id, userId);
    }

    @DeleteMapping("/films/{id}/like/{userId}")
    public void deleteFilmLike(@PathVariable long id, @PathVariable long userId) throws ValidationException {
        userController.findUserByID(userId);
        filmService.deleteFilmLike(id, userId);
    }

    @GetMapping("/films/popular")
    public List<Film> findPopularFilms (@Positive @RequestParam (required = false) String count){
        if (count == null) {
            return filmService.findPopularFilms(0);
        }
        return filmService.findPopularFilms(Integer.parseInt(count));
    }
}
