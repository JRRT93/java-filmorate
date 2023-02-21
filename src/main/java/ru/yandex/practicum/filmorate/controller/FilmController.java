package ru.yandex.practicum.filmorate.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.dao.InMemoryFilmStorage;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.service.FilmValidationService;

import javax.validation.Valid;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@RestController
@Slf4j
@Validated
public class FilmController {
    private final FilmValidationService filmValidationService = new FilmValidationService();
    private final InMemoryFilmStorage inMemoryFilmStorage = new InMemoryFilmStorage();


    /**
     * Метод создает и возвращает ArrayList, в который складывает Film из TreeMap в порядке возрастания ID.
     */
    @GetMapping("/films")
    public List<Film> getAllFilms() {
        log.info("GET request for /films path received");
        List<Film> listOfFilms = new ArrayList<>();
        for (Map.Entry<Integer, Film> entry : inMemoryFilmStorage.getFilms().entrySet()) {
            listOfFilms.add(entry.getValue());
        }
        log.debug("List of films provided from TreeMap");
        return listOfFilms;
    }

    /**
     * Film из запроса проходит валидацию полей нотациями String, при неудаче ответ клиенту с кодом 400. При успехе
     * метод вызывает валидацию оставшихся полей методом validateFilm класса FilmValidationService. При неудачной
     * валидации выбрасывается кастомное исключение ValidationException и ответ клиенту с кодом 500. При успехе вызывается
     * метод addFilm класса InMemoryFilmStorage и клиенту возвращается добавленный Film.
     */
    @PostMapping("/films")
    public Film addNewFilm (@RequestBody @Valid Film film) throws ValidationException {
        log.info("POST request for /films path received");
        filmValidationService.validateFilm(film);
        log.debug("All fields for FILM validated successful");
        film = inMemoryFilmStorage.addFilm(film);
        log.debug("FILM successful added. ID=" + film.getId());
        return film; //todo проверить добавляются ли косячные запросы в мапу и убрать
    }

    /**
     * Film из запроса проходит валидацию полей нотациями String, при неудаче ответ клиенту с кодом 400. При успехе
     * метод вызывает валидацию оставшихся полей методом validateFilm класса FilmValidationService. При неудачной
     * валидации выбрасывается кастомное исключение ValidationException и ответ клиенту с кодом 500. При успехе вызывается
     * метод updateFilm класса InMemoryFilmStorage и клиенту возвращается обновленный Film.
     */
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
