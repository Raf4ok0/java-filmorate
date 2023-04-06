package ru.yandex.practicum.filmorate.Validators;

import lombok.extern.slf4j.Slf4j;
import ru.yandex.practicum.filmorate.Exception.ValidationException;
import ru.yandex.practicum.filmorate.model.Film;

import java.time.LocalDate;

@Slf4j
public class FilmValidator {

    private static final LocalDate EARLIEST_RELEASE_DATE = LocalDate.of(1895, 12, 28);

    public static void validate(Film film) {
        if (film.getName() == null || film.getName().isBlank()) {
            log.error("Имя фильма не заполнено");
            throw new ValidationException("Ошибка валидации");
        } else if (film.getDescription() == null || film.getDescription().length() > 200) {
            log.error("Некорректное описание");
            throw new ValidationException("Ошибка валидации");
        } else if (film.getReleaseDate() == null || film.getReleaseDate().isBefore(EARLIEST_RELEASE_DATE)) {
            log.error("Дата релиза незаполнена или раньше 1895-12-28");
            throw new ValidationException("Ошибка валидации");
        } else if (film.getDuration() <= 0) {
            log.error("Длительность меньше 0");
            throw new ValidationException("Ошибка валидации");
        }
    }
}
