package ru.yandex.practicum.filmorate.Validators;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ru.yandex.practicum.filmorate.Exception.NotFoundException;
import ru.yandex.practicum.filmorate.model.Film;

import java.time.LocalDate;
import java.util.Map;

public class FilmValidator {

    private static final Logger log = LoggerFactory.getLogger(FilmValidator.class);
    private static final LocalDate earliestReleaseDate = LocalDate.of(1895, 12, 28);

    public static boolean validate(Film film) {
        if (film.getName() == null || film.getName().isBlank()) {
            log.error("Имя фильма не заполнено");
            return false;
        } else if (film.getDescription() == null || film.getDescription().length() > 200) {
            log.error("Некорректное описание");
            return false;
        } else if (film.getReleaseDate() == null || film.getReleaseDate().isBefore(earliestReleaseDate)) {
            log.error("Дата релиза незаполнена или раньше 1895-12-28");
            return false;
        } else if (film.getDuration() <= 0) {
            log.error("Длительность меньше 0");
            return false;
        } else return true;
    }

    public static void checkIfFilmExists(Film film, Map<Integer, Film> films) {
        if (!films.containsKey(film.getId())) {
            log.info("Проверка, существует ли фильм");
            throw new NotFoundException("Такого фильма нет");
        }
    }
}
