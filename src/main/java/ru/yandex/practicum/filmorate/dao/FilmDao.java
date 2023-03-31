package ru.yandex.practicum.filmorate.dao;

import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.Film;

import java.util.List;

public interface FilmDao {
    Long createFilm(Film film);

    boolean updateFilm(Film film);

    Film findFilmById(long id) throws ValidationException;

    List<Film> findAllFilms();
}
