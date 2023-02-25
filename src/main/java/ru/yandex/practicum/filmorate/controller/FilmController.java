package ru.yandex.practicum.filmorate.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.dao.InMemoryFilmStorage;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.service.FilmValidationService;

import javax.validation.Valid;
import java.util.List;

@RestController
@Slf4j
@Validated
public class FilmController {
    private final FilmValidationService filmValidationService = new FilmValidationService();
    private final InMemoryFilmStorage inMemoryFilmStorage = new InMemoryFilmStorage();

    @GetMapping("/films")
    public List<Film> getAllFilms() {
        log.info("GET request for /films path received");
        return inMemoryFilmStorage.getAllFilms();
    }

    @PostMapping("/films")
    public Film addNewFilm (@RequestBody @Valid Film film) throws ValidationException {
        log.info("POST request for /films path received");
        filmValidationService.validateFilm(film);
        log.debug("All fields for FILM validated successful");
        film = inMemoryFilmStorage.addFilm(film);
        log.debug("FILM successful added. ID=" + film.getId());
        return film;
    }

    @PutMapping("/films")
    public Film updateFilm (@RequestBody @Valid Film film) throws ValidationException {
        log.info("PUT request for /films path received");
        filmValidationService.validateFilm(film);
        log.debug("All fields for FILM validated successful");
        film = inMemoryFilmStorage.updateFilm(film);
        log.debug("FILM successful updated. ID=" + film.getId());
        return film;
    }
}
