package ru.yandex.practicum.filmorate.storage;

import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.Film;

import java.util.List;

public interface FIlmStorage {
    Long addFilm(Film film);

    boolean updateFilm(Film film) throws ValidationException;

    Film findFilmById(long id) throws ValidationException;

    List<Film> getAllFilms();
}