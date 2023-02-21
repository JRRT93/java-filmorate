package ru.yandex.practicum.filmorate.dao;

import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.User;

import java.util.Map;
import java.util.TreeMap;

@lombok.Getter
@lombok.NoArgsConstructor
public class InMemoryUserStorage {
    private final Map<Integer, User> users = new TreeMap<>();
    private int id = 1;

    /**
     * Метод предназначен для добавления User, пришедшего из POST запроса /users. Устанавливается ID в порядке добавления.
     * Проводится дополнительная проверка поля name, если поле не проинициализировано или пустое, то name присваивается
     * значение поля login. Затем user добавляется в TreeMap, где идёт сортировка по натуральному порядку ключа (ID).
     * Затем ID увеличивается на 1 для добавления следующего User. Метод возвращает User с установленным ID и, при
     * необходимости, с установленным полем name.
     */
    public User addUser(User user) {
        user = checkUsersName(user);
        user.setId(id);
        users.put(id, user);
        ++id;
        return user;
    }

    /**
     * Метод предназначен для обновления User, пришедшего из PUT запроса /users. Пытаюсь вызвать из TreeMap user с ключом
     * ID, который указан в теле PUT запроса, если результат не NULL, то старый user для этого ключа заменяется новым и
     * метод возвращает user с обновленными полями. Если результат обращения к Map ==null, выбрасывается кастомное исключение
     * ValidationException
     */
    public User updateUser(User user) throws ValidationException {
        user = checkUsersName(user);
        if (users.get(user.getId()) != null) {
            users.replace(user.getId(), user);
            return user;
        } else {
            throw new ValidationException("Incorrect ID. This user is not in database yet");
        }
    }

    /**
     * Метод предназначен для дополнительной проверки поля name, если поле не проинициализировано или пустое, то name
     * присваивается значение поля login. Затем метод возвращает user для дальнейшей работы
     */
    private User checkUsersName (User user) {
        if (user.getName() == null || user.getName().isBlank()) {
            user.setName(user.getLogin());
        }
        return user;
    }
}
