package ru.yandex.practicum.filmorate.Controllers;

import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.Exception.AlreadyExistException;
import ru.yandex.practicum.filmorate.Exception.NotExistException;
import ru.yandex.practicum.filmorate.Validators.FilmValidator;
import ru.yandex.practicum.filmorate.model.Film;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

@Slf4j
@RestController
@RequestMapping("/films")
public class FilmController {
    private final Map<Long, Film> films = new HashMap<>();
    long filmId = 0L;

    @GetMapping
    public Collection<Film> allFilms() {
        return new ArrayList<>(films.values());
    }

    @PostMapping
    public Film create(@RequestBody Film film) {
        FilmValidator.validate(film);
        if (!films.containsKey(film.getId())) {
            long id = generateFilmId();
            film.setId(id);
            films.put(film.getId(), film);
            log.info("Добавлен фильм " + film.getName());
            return film;
        }
        throw new AlreadyExistException("Фильм " +
                film.getName() + " уже добавлен.");
    }

    @PutMapping
    public Film put(@RequestBody Film film) {
        FilmValidator.validate(film);
        if (films.containsKey(film.getId())) {
            films.put(film.getId(), film);
            log.info("Обновлены данные фильма " + film.getName());
            return film;
        }
        throw new NotExistException("Такого фильма нет");
    }

    public long generateFilmId() {
        filmId++;
        return filmId;
    }

}
