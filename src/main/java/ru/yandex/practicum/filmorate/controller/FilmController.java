package ru.yandex.practicum.filmorate.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.service.FilmService;

import javax.validation.Valid;
import java.util.Collection;

@Slf4j
@RestController
@RequestMapping("films")
@RequiredArgsConstructor
public class FilmController {

    private final FilmService filmService;

    @GetMapping
    public Collection<Film> getAllFilms() {
        return filmService.getAllFilms();
    }

    @PostMapping
    private Film createFilm(@Valid @RequestBody Film film) {
        return filmService.createFilm(film);
    }

    @PutMapping
    private Film updateFilm(@Valid @RequestBody Film film) {
        return filmService.updateFilm(film);
    }

    @PutMapping("/{id}/like/{userId}")
    private void addLikeToFilm(@PathVariable int id,
                               @PathVariable int userId) {
        filmService.addLikeToFilm(id, userId);
    }

    @DeleteMapping("/{id}/like/{userId}")
    private void deleteLikeOfFilm(@PathVariable int id,
                                  @PathVariable int userId) {
        filmService.deleteLikeOfFilm(filmId, userId);
    }

    @GetMapping("/popular/")
    private Collection<Film> getMostPopular(@RequestParam(defaultValue = "10") String count) {
        int intCount = Integer.parseInt(count);
        return filmService.getMostPopular(intCount);
    }
}
