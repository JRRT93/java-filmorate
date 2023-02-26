package ru.yandex.practicum.filmorate.storage;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.Film;

import java.util.*;

@Getter
@NoArgsConstructor
@Component
@Slf4j
public class InMemoryFilmStorage implements FIlmStorage {
    private final Map<Long, Film> films = new TreeMap<>();

    public void addFilm(Film film) {
        if(film.getLikes() == null) film.setLikes(new TreeSet<>());
        log.debug("LIKES field initialized");
        films.put(film.getId(), film);
    }

    public void updateFilm(Film film) throws ValidationException {
        checkFilmInStorage(film.getId());
        if(film.getLikes() == null) film.setLikes(new TreeSet<>());
        log.debug("LIKES field initialized");
        films.replace(film.getId(), film);
    }

    public Film findFilmByID(long id) throws ValidationException {
        checkFilmInStorage(id);
        return films.get(id);
    }

    public List<Film> getAllFilms() {
        return new ArrayList<>(films.values());
    }

    private void checkFilmInStorage(long id) throws ValidationException {
        if (films.get(id) == null) {
            throw new ValidationException("Incorrect ID " + id + ". This film is not in database yet");
        }
    }
}
