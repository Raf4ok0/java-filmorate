package ru.yandex.practicum.filmorate.Service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.Exception.NotFoundException;
import ru.yandex.practicum.filmorate.Storage.FilmStorage;
import ru.yandex.practicum.filmorate.Validators.FilmValidator;
import ru.yandex.practicum.filmorate.model.Film;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class FilmService {

    private final FilmStorage storage;

    public List<Film> getAllFilmsList() {
        return storage.getAllFilmsList();
    }

    public Film getById(long id) {
        return storage.getById(id);
    }

    public Film addFilm(Film film) {
        FilmValidator.validate(film);
        return storage.addFilm(film);
    }

    public Film updateFilm(Film film) {
        FilmValidator.validate(film);
        return storage.updateFilm(film);
    }

    public Film deleteFilm(long id) {
        return storage.deleteFilm(id);
    }

    public Film addLike(long userId, long filmId) throws NotFoundException {
        checkExistFilm(filmId);
        storage.addLike(filmId, userId);
        log.info("Лайк добавлен");
        return storage.getById(filmId);
    }

    public Film deleteLike(long filmId, long userId) throws NotFoundException {
        checkExistFilm(filmId);
        storage.removeLike(filmId, userId);
        log.info("Лайк удален");
        return storage.getById(filmId);
    }

    public List<Film> getTopLikedFilmsList(long count) {
        List<Film> popular = new ArrayList<>(storage.getAllFilmsList());
        return popular.stream()
                .sorted(this::compare)
                .limit(count)
                .collect(Collectors.toList());
    }

    private int compare(Film film1, Film film2) {
        return (film1.getLikes().size() - film2.getLikes().size()) * -1;
    }

    public void checkExistFilm(long filmId) {
        if (storage.getById(filmId) == null) {
            throw new NotFoundException("Фильм с таким ID не найден.");
        }
    }
}
