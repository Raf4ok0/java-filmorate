package ru.yandex.practicum.filmorate.Controllers;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.Service.FilmService;
import ru.yandex.practicum.filmorate.model.Film;

import java.util.Collection;
import java.util.List;

@Slf4j
@RestController
@RequestMapping("/films")
@RequiredArgsConstructor
public class FilmController {

    private final FilmService filmService;

    @GetMapping
    public List<Film> getAllFilms() {
        log.info("getAllFilms");
        return filmService.getAllFilmsList();
    }

    @GetMapping("/{id}")
    public Film getFilm(@PathVariable long id) {
        log.info("getById #{}", id);
        return filmService.getById(id);
    }

    @PostMapping
    public Film create(@RequestBody Film film) {
        log.info("addFilm");
        return filmService.addFilm(film);
    }

    @PutMapping
    public Film put(@RequestBody Film film) {
        log.info("updateFilm");
        return filmService.updateFilm(film);
    }

    @DeleteMapping("/{filmId}")
    public Film delete(@PathVariable long filmId) {
        log.info("updateFilm");
        return filmService.deleteFilm(filmId);
    }

    @PutMapping("/{id}/like/{userId}")
    public Film addLike(@PathVariable long id, @PathVariable long userId) {
        log.info("addLike");
        return filmService.addLike(userId, id);
    }

    @DeleteMapping("/{id}/like/{userId}")
    public Film deleteLike(@PathVariable long id, @PathVariable long userId) {
        log.info("deleteLike");
        return filmService.deleteLike(userId, id);
    }

    @GetMapping("/popular")
    public Collection<Film> getPopularFilms(@RequestParam(defaultValue = "10") Integer count) {
        log.info("getTopLikedFilmsList");
        return filmService.getTopLikedFilmsList(count);
    }
}
