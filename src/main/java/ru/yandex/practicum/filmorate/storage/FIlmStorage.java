package ru.yandex.practicum.filmorate.storage;

import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.Film;

import java.util.List;

public interface FIlmStorage {
    void addFilm(Film film);
    void updateFilm(Film film) throws ValidationException;
    List<Film> getAllFilms();
    Film findFilmById(long id) throws ValidationException;
}
