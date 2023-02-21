package ru.yandex.practicum.filmorate.service;

import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.Film;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

@Component //todo а здесь вообще нужна аннотация Component? Спринг же не использует этот класс
public class FilmValidationService {
    final static int DESCRIPTION_MAX_LENGTH = 200;
    final static LocalDate ALLOWED_EARLIER_REALISE_DATE = LocalDate.of(1895, 12, 28);
    final static DateTimeFormatter FORMATTER = DateTimeFormatter.ofPattern("dd.MM.yyyy");

    /**
     * В методе валидируются поля Film. Поля name и duration валидируются автоматически с помощью аннотаций Spring.
     * Поля description (длина описания не должна превышать установленный лимит) и releaseDate (дата выхода должны быть
     * не ранее, чем установленная дата) проверяются в этом методе. Критерии проверки задаются в качестве констант. Если
     * валидация не проходит, то метод выбрасывает кастомное исключение ValidationException.
     */
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
