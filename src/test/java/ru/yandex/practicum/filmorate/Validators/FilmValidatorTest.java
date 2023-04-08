package ru.yandex.practicum.filmorate.Validators;

import org.junit.jupiter.api.Test;
import ru.yandex.practicum.filmorate.Exception.ValidationException;
import ru.yandex.practicum.filmorate.model.Film;

import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.*;

class FilmValidatorTest {

    Film film;

    void initFilm() {
        film = new Film(1L, "Унесенные ветром", "Описание до 200 символов",
                LocalDate.of(1985, 12, 22), 200);
    }

    @Test
    void filmNameCantBeEmpty() {
        initFilm();
        Film film2 = new Film(2L, "", "Описание до 200 символов",
                LocalDate.of(1985, 12, 22), 200);
        assertDoesNotThrow(() -> FilmValidator.validate(film));
        assertThrows(ValidationException.class, () -> FilmValidator.validate(film2));
    }

    @Test
    void filmDescriptionCantBeNullOrMoreThan200Characters() {
        initFilm();
        Film film2 = new Film(2L, "Аватар", "Описание больше 200 символов. " +
                "Описание больше 200 символов. Описание больше 200 символов. Описание больше 200 символов. " +
                "Описание больше 200 символов. Описание больше 200 символов. Описание больше 200 символов.",
                LocalDate.of(2009, 12, 22), 200);
        Film film3 = new Film(3L, "Аватар", null,
                LocalDate.of(2009, 12, 22), 200);
        assertDoesNotThrow(() -> FilmValidator.validate(film));
        assertThrows(ValidationException.class, () -> FilmValidator.validate(film2));
        assertThrows(ValidationException.class, () -> FilmValidator.validate(film3));
    }

    @Test
    void filmReleaseDateShouldBeAfter18951228() {
        initFilm();
        Film film2 = new Film(2L, "Аватар", "Описание до 200 символов",
                LocalDate.of(1895, 12, 27), 200);
        Film film3 = new Film(3L, "Аватар", "Описание до 200 символов",
                LocalDate.of(1895, 12, 28), 200);
        assertDoesNotThrow(() -> FilmValidator.validate(film));
        assertThrows(ValidationException.class, () -> FilmValidator.validate(film2));
        assertDoesNotThrow(() -> FilmValidator.validate(film3));
    }

    @Test
    void filmDurationShouldBePositive() {
        initFilm();
        Film film2 = new Film(2L, "Аватар", "Описание до 200 символов",
                LocalDate.of(1995, 12, 27), -200);
        Film film3 = new Film(3L, "Аватар2", "Описание до 200 символов",
                LocalDate.of(1995, 12, 28), 0);
        assertDoesNotThrow(() -> FilmValidator.validate(film));
        assertThrows(ValidationException.class, () -> FilmValidator.validate(film2));
        assertThrows(ValidationException.class, () -> FilmValidator.validate(film3));
    }
}