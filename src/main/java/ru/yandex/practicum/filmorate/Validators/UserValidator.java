package ru.yandex.practicum.filmorate.Validators;

import lombok.extern.slf4j.Slf4j;
import ru.yandex.practicum.filmorate.Exception.ValidationException;
import ru.yandex.practicum.filmorate.model.User;

import java.time.LocalDate;
import java.util.regex.Pattern;

@Slf4j
public class UserValidator {
    private static final String EMAIL_PATTERN = "^(.+)@(\\S+)$";
    private static final Pattern LOGIN_PATTERN = Pattern.compile("\\s");

    public static void validate(User user) {
        if (user.getName() == null || user.getName().isBlank())
            user.setName(user.getLogin());
        if (user.getEmail() == null || user.getEmail().isBlank()) {
            log.error("Поле почты не заполнено");
            throw new ValidationException("Ошибка валидации");
        } else if (!patternMatches(user.getEmail(), EMAIL_PATTERN)) {
            log.error("Поле почты не корректно");
            throw new ValidationException("Ошибка валидации");
        } else if (user.getLogin() == null || user.getLogin().isBlank() || user.getLogin().isBlank()) {
            log.error("Логин не заполнен");
            throw new ValidationException("Ошибка валидации");
        } else if (LOGIN_PATTERN.matcher(user.getLogin()).find()) {
            log.error("Логин может быть с пробелом");
            throw new ValidationException("Ошибка валидации");
        } else if (user.getBirthday() == null || user.getBirthday().isAfter(LocalDate.now())) {
            log.error("Дата рождения некорректна");
            throw new ValidationException("Ошибка валидации");
        }
    }

    public static boolean patternMatches(String emailAddress, String emailPattern) {
        return Pattern.compile(emailPattern)
                .matcher(emailAddress)
                .matches();
    }
}
