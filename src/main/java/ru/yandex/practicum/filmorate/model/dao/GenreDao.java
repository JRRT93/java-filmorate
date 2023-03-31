package ru.yandex.practicum.filmorate.model.dao;

import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.Genre;

import java.util.List;

public interface GenreDao {
    List<Genre> getAllGenres();

    Genre findGenreById(long id) throws ValidationException;
}
