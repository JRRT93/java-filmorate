package ru.yandex.practicum.filmorate.model;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Positive;
import java.time.LocalDate;

/**
 * В классе используются аннотации Lombok: Getter (чтобы можно было вытаскивать поля в других классах), Setter (чтобы
 * можно было устанавливать ID в классе InMemoryFilmStorage), AllArgsConstructor и RequiredArgsConstructor не понимаю
 * зачем добавил - так было на вебинаре.
 */
@lombok.Getter
@lombok.Setter
@lombok.AllArgsConstructor
@lombok.RequiredArgsConstructor
@lombok.EqualsAndHashCode
public class Film {
    /**
     * Для полей используются аннотации Spring: @Positive и @NotBlank для автоматической валидации при получении запроса с
     * объектом от клиента. Остальные поля валидируются специальным методом validateFilm класса FilmValidationService
     */
    private int id;
    @NotBlank
    private String name;
    private String description;
    @Positive
    private int duration;
    private LocalDate releaseDate;
}
