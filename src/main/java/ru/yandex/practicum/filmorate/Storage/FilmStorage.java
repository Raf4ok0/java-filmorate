package ru.yandex.practicum.filmorate.Storage;

import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.User;

import java.util.List;
import java.util.Map;

public interface FilmStorage {

    Film addFilm(Film film);

    Film updateFilm(Film film);

    void deleteFilm(long id);

    List<Film> getAllFilmsList();

    Film getById(long id);
}
