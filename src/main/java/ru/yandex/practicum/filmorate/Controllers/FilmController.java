package ru.yandex.practicum.filmorate.Controllers;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.Service.FilmService;
import ru.yandex.practicum.filmorate.model.Film;

import java.util.Collection;
import java.util.List;

@Slf4j
@RestController
@RequestMapping("/films")
public class FilmController {

    private final FilmService filmService;

    @Autowired
    public FilmController(FilmService filmService) {
        this.filmService = filmService;
    }

    @GetMapping
    public List<Film> getAllFilms() {
        return filmService.inMemoryFilmStorage.getAllFilmsList();
    }

    @GetMapping("/{id}")
    public ResponseEntity<Film> getFilm(@PathVariable long id) {
        return new ResponseEntity<>(filmService.inMemoryFilmStorage.getById(id), HttpStatus.OK);
    }

    @PostMapping
    public Film create(@RequestBody Film film) {
        filmService.inMemoryFilmStorage.addFilm(film);
        return film;
    }

    @PutMapping
    public ResponseEntity put(@RequestBody Film film) {
        filmService.inMemoryFilmStorage.updateFilm(film);
        return ResponseEntity.ok(film);
    }

    @DeleteMapping("/{filmId}")
    public void delete(@PathVariable long filmId) {
        filmService.inMemoryFilmStorage.deleteFilm(filmId);
    }

    @PutMapping("/{id}/like/{userId}")
    public ResponseEntity<Film> addLike(@PathVariable long id, @PathVariable long userId) {
        return filmService.addLike(userId, id);
    }

    @DeleteMapping("/{id}/like/{userId}")
    public ResponseEntity<Film> deleteLike(@PathVariable long id, @PathVariable long userId) {
        return filmService.deleteLike(userId, id);
    }

    @GetMapping("/popular")
    public Collection<Film> getPopularFilms(@RequestParam(defaultValue = "10") Integer count) {
        return filmService.getTopLikedFilmsList(count);
    }
}
