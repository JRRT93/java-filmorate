package ru.yandex.practicum.filmorate.dao;

import lombok.Getter;
import lombok.NoArgsConstructor;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.Film;

import java.util.List;
import java.util.Map;
import java.util.TreeMap;

@Getter
@NoArgsConstructor
public class InMemoryFilmStorage {
    private final Map<Integer, Film> films = new TreeMap<>();
    private int id = 1;

    public Film addFilm(Film film) {
        film.setId(id);
        films.put(id, film);
        ++id;
        return film;
    }

    public Film updateFilm(Film film) throws ValidationException {
        if (films.get(film.getId()) != null) {
            films.replace(film.getId(), film);
            return film;
        } else {
            throw new ValidationException("Incorrect ID. This film is not in database yet");
        }
    }

    public List<Film> getAllFilms() {
        return List.copyOf(films.values());
    }
}
