package ru.yandex.practicum.filmorate.Validators;

import org.junit.jupiter.api.Test;
import ru.yandex.practicum.filmorate.model.Film;

import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.*;

class FilmValidatorTest {

    Film film;

    void initFilm() {
        film = new Film("Унесенные ветром", "Описание до 200 символов",
                LocalDate.of(1985, 12,22), 200);
    }

    @Test
    void filmNameCantBeEmpty() {
        initFilm();
        Film film2 = new Film( "", "Описание до 200 символов",
                LocalDate.of(1985, 12,22), 200);
        assertTrue(FilmValidator.validate(film));
        assertFalse(FilmValidator.validate(film2));
    }

    @Test
    void filmDescriptionCantBeNullOrMoreThan200Characters() {
        initFilm();
        Film film2 = new Film( "Аватар", "Описание больше 200 символов. " +
                "Описание больше 200 символов. Описание больше 200 символов. Описание больше 200 символов. " +
                "Описание больше 200 символов. Описание больше 200 символов. Описание больше 200 символов.",
                LocalDate.of(2009, 12,22), 200);
        Film film3 = new Film( "Аватар", null,
                LocalDate.of(2009, 12,22), 200);
        assertTrue(FilmValidator.validate(film));
        assertFalse(FilmValidator.validate(film2));
        assertFalse(FilmValidator.validate(film3));
    }

    @Test
    void filmReleaseDateShouldBeAfter18951228() {
        initFilm();
        Film film2 = new Film( "Аватар", "Описание до 200 символов",
                LocalDate.of(1895, 12,27), 200);
        Film film3 = new Film( "Аватар", "Описание до 200 символов",
                LocalDate.of(1895, 12,28), 200);
        assertTrue(FilmValidator.validate(film));
        assertFalse(FilmValidator.validate(film2));
        assertTrue(FilmValidator.validate(film3));
    }

    @Test
    void filmDurationShouldBePositive() {
        initFilm();
        Film film2 = new Film( "Аватар", "Описание до 200 символов",
                LocalDate.of(1995, 12,27), -200);
        Film film3 = new Film( "Аватар2", "Описание до 200 символов",
                LocalDate.of(1995, 12,28), 0);
        assertTrue(FilmValidator.validate(film));
        assertFalse(FilmValidator.validate(film2));
        assertFalse(FilmValidator.validate(film3));
    }
}