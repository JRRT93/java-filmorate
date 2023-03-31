package ru.yandex.practicum.filmorate.storage;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.Film;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

@Getter
@NoArgsConstructor
@Slf4j
public class InMemoryFilmStorageTest implements FIlmStorage {
    private final Map<Long, Film> films = new TreeMap<>();
    private long id = 1;

    public Long addFilm(Film film) {
        film.setId(id);
        id++;
        films.put(film.getId(), film);
        return film.getId();
    }

    public boolean updateFilm(Film film) throws ValidationException {
        Film oldValueFilm = films.get(film.getId());
        if (oldValueFilm == null) {
            throw new ValidationException("Incorrect ID " + film.getId() + ". This film is not in database yet");
        }
        return films.replace(film.getId(), oldValueFilm, film);
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