package ru.yandex.practicum.filmorate.dao;

import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.Rating;

import java.util.List;

public interface RatingDao {
    List<Rating> getAllRatings();

    Rating findRatingById(long id) throws ValidationException;
}
