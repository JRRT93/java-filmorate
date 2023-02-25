package ru.yandex.practicum.filmorate.service;

import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.Film;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

public class FilmValidationService {
    final static int DESCRIPTION_MAX_LENGTH = 200;
    final static LocalDate ALLOWED_EARLIER_REALISE_DATE = LocalDate.of(1895, 12, 28);
    final static DateTimeFormatter FORMATTER = DateTimeFormatter.ofPattern("dd.MM.yyyy");

    public void validateFilm(Film film) throws ValidationException {
        if (film.getDescription().length() > DESCRIPTION_MAX_LENGTH) {
            throw new ValidationException("Incorrect film description: maximum length is " + DESCRIPTION_MAX_LENGTH
                    + " characters");
        }
        if (film.getReleaseDate().isBefore(ALLOWED_EARLIER_REALISE_DATE)) {
            throw new ValidationException("Incorrect film release date: date should be after "
                    + FORMATTER.format(ALLOWED_EARLIER_REALISE_DATE));
        }
    }
}
