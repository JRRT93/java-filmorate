package ru.yandex.practicum.filmorate.storage;

import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.Film;

import java.util.*;

@Getter
@NoArgsConstructor
@Component
public class InMemoryFilmStorage implements FIlmStorage {
    private final Map<Long, Film> films = new TreeMap<>();

    public void addFilm(Film film) {
        films.put(film.getId(), film);
    }

    public void updateFilm(Film film) throws ValidationException {
        if (films.get(film.getId()) == null) {
            throw new ValidationException("Incorrect ID " + film.getId() + ". This film is not in database yet");
        }
        films.replace(film.getId(), film);
    }

    public Film findFilmById(long id) throws ValidationException {
        if (films.get(id) == null) {
            throw new ValidationException("Incorrect ID " + id + ". This film is not in database yet");
        }
        return films.get(id);
    }

    public List<Film> getAllFilms() {
        return new ArrayList<>(films.values());
    }
}