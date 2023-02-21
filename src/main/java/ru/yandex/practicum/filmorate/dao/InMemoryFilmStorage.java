package ru.yandex.practicum.filmorate.dao;

import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.Film;

import java.util.Map;
import java.util.TreeMap;

@lombok.Getter
@lombok.NoArgsConstructor
public class InMemoryFilmStorage {
    private final Map<Integer, Film> films = new TreeMap<>();
    private int id = 1;

    /**
     * Метод предназначен для добавления Film, пришедшего из POST запроса /films. Устанавливается ID в порядке добавления,
     * затем фильм добавляется в TreeMap, где идёт сортировка по натуральному порядку ключа (ID). Затем ID увеличивается
     * на 1 для добавления следующего Film. Метод возвращает Film с установленным ID.
     */
    public Film addFilm(Film film) {
        film.setId(id);
        films.put(id, film);
        ++id;
        return film;
    }

    /**
     * Метод предназначен для обновления Film, пришедшего из PUT запроса /films. Пытаюсь вызвать из TreeMap фильм с ключом
     * ID, который указан в теле PUT запроса, если результат не NULL, то старый Film для этого ключа заменяется новым и
     * метод возвращает Film с обновленными полями. Если результат обращения к Map ==null, выбрасывается кастомное исключение
     * ValidationException
     */
    public Film updateFilm(Film film) throws ValidationException {
        if (films.get(film.getId()) != null) {
            films.replace(film.getId(), film);
            return film;
        } else {
            throw new ValidationException("Incorrect ID. This film is not in database yet");
        }
    }
}
