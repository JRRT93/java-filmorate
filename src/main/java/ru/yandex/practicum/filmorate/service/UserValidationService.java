package ru.yandex.practicum.filmorate.service;

import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.User;

@Component //todo а здесь вообще нужна аннотация Component? Спринг же не использует этот класс
public class UserValidationService {

    /**
     * В методе валидируются поля User. Поле birthday валидируется автоматически с помощью аннотаций Spring. Поля
     * email и login частично валидируется через аннотацию String - проводится проверка, чтобы были не пустые строки.
     * В данном методе для email проводится вторая часть проверки - чтобы mail содержал @. Для login роводится вторая
     * часть проверки - чтобы login не содержал пробелов.
     * Да, я понимаю, что это выглядит криво, но как проводить сложные проверки аннотациями Spring нам ещё не рассказали,
     * а всю валидацию проводить в этом методе не хочется - хочется попробовать как работают аннотации.
     * При успешной проверке ничего не происходит, при неудачной проверке метод выбрасывает кастомное исключение
     * ValidationException.
     */
    public void validateUser(User user) throws ValidationException {
        if (!user.getEmail().contains("@")) {
            throw new ValidationException("Email should contain @!");
        }
        if (user.getLogin().contains(" ")) {
            throw new ValidationException("Login should not contain spaces!");
        }
    }
}
