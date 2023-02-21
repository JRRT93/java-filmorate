package ru.yandex.practicum.filmorate.model;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Past;
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
public class User {
    /**
     * Для полей используются аннотации Spring: @Positive, @Past и @NotBlank для автоматической валидации при получении
     * запроса с объектом от клиента. Остальные поля валидируются специальным методом validateUser класса UserValidationService
     */
    private int id;
    @NotBlank
    @Email
    private String email;
    @NotBlank
    private String login;
    private String name;
    @Past
    private LocalDate birthday;
}
