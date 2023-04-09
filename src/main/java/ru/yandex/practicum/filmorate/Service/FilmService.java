package ru.yandex.practicum.filmorate.Service;

import lombok.extern.slf4j.Slf4j;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.Exception.NotFoundException;
import ru.yandex.practicum.filmorate.Storage.FilmStorage;
import ru.yandex.practicum.filmorate.model.Film;
import org.springframework.http.ResponseEntity;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
public class FilmService {

    public final FilmStorage inMemoryFilmStorage;

    @Autowired
    public FilmService(FilmStorage inMemoryFilmStorage) {
        this.inMemoryFilmStorage = inMemoryFilmStorage;
    }

    public ResponseEntity<Film> addLike(long userId, long filmId) throws NotFoundException {
        checkExistFilm(filmId);
        inMemoryFilmStorage.getById(filmId).getLikes().add(userId);
        log.info("Лайк добавлен");
        return new ResponseEntity<>(inMemoryFilmStorage.getById(filmId), HttpStatus.OK);
    }

    public ResponseEntity<Film> deleteLike(long filmId, long userId) throws NotFoundException {
        checkExistFilm(filmId);
        inMemoryFilmStorage.getById(filmId).getLikes().remove(userId);
        log.info("Лайк удален");
        return new ResponseEntity<>(inMemoryFilmStorage.getById(filmId), HttpStatus.OK);
    }

    public List<Film> getTopLikedFilmsList(long count) {
        List<Film> popular = new ArrayList<>(inMemoryFilmStorage.getAllFilmsList());
        return popular.stream()
                .sorted(this::compare)
                .limit(count)
                .collect(Collectors.toList());
    }

    private int compare(Film film1, Film film2) {
        return (film1.getLikes().size() - film2.getLikes().size()) * -1;
    }

    public void checkExistFilm(long filmId) {
        if (inMemoryFilmStorage.getById(filmId) == null) {
            throw new NotFoundException("Фильм с таким ID не найден.");
        }
    }
}
