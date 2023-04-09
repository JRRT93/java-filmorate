package ru.yandex.practicum.filmorate.dao;

import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.Film;

import java.util.List;
import java.util.Optional;

public interface FilmDao {
    Long createFilm(Film film);

    boolean updateFilm(Film film);

    Optional<Film> findFilmById(long id) throws ValidationException;

    List<Film> findAllFilms();
}
