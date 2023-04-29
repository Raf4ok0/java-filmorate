package ru.yandex.practicum.filmorate.Storage;

import ru.yandex.practicum.filmorate.model.Film;

import java.util.List;

public interface FilmStorage {

    Film addFilm(Film film);

    Film updateFilm(Film film);

    Film deleteFilm(long id);

    List<Film> getAllFilmsList();

    Film getById(long id);

    Film addLike(long filmId, long userId);

    Film removeLike(long filmId, long userId);
}

