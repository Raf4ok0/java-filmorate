package ru.yandex.practicum.filmorate.Storage;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.Exception.AlreadyExistException;
import ru.yandex.practicum.filmorate.Exception.NotFoundException;
import ru.yandex.practicum.filmorate.model.Film;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Slf4j
@Component
public class InMemoryFilmStorage implements FilmStorage {

    private final Map<Long, Film> films = new HashMap<>();
    long filmId = 0;

    @Override
    public List<Film> getAllFilmsList() {
        return new ArrayList<>(films.values());
    }

    @Override
    public Film getById(long id) {
        if (!films.containsKey(id)) {
            throw new NotFoundException("Такого фильма нет");
        }
        return films.get(id);
    }

    @Override
    public Film addFilm(Film film) {
        if (films.containsKey(film.getId())) {
            throw new AlreadyExistException("Фильм " +
                    film.getName() + " уже добавлен.");
        } else {
            long id = generateFilmId();
            film.setId(id);
            films.put(film.getId(), film);
            log.info("Добавлен фильм " + film.getName());
        }
        return film;
    }

    @Override
    public Film updateFilm(Film film) {
        if (!films.containsKey(film.getId())) {
            throw new NotFoundException("Фильм " +
                    film.getName() + " не найден.");
        }
        films.put(film.getId(), film);
        log.info("Обновлены данные фильма " + film.getName());
        return film;
    }

    @Override
    public Film deleteFilm(long id) {
        if (films.containsKey(id)) {
            return films.remove(id);
        } else {
            throw new NotFoundException("Такого фильма нет");
        }
    }

    public long generateFilmId() {
        filmId++;
        return filmId;
    }
}
