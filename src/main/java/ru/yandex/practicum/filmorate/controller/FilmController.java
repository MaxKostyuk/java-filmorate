package ru.yandex.practicum.filmorate.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.service.FilmService;

import javax.validation.Valid;
import javax.validation.constraints.Positive;
import java.util.List;

@Slf4j
@RestController
@RequestMapping("films")
@Validated
@RequiredArgsConstructor
public class FilmController {

    private final FilmService service;

    @GetMapping
    public List<Film> getAll() {
        return service.getAllFilms();
    }

    @PostMapping
    public Film create(@Valid @RequestBody Film film) {
        return service.createFilm(film);
    }

    @PutMapping
    public Film update(@Valid @RequestBody Film film) {
        return service.updateFilm(film);
    }

    @GetMapping("/{id}")
    public Film getById(@PathVariable int id) {
        return service.getById(id);
    }

    @PutMapping("/{id}/like/{userId}")
    public void addLike(@PathVariable int id,
                         @PathVariable int userId) {
        service.addLikeToFilm(id, userId);
    }

    @DeleteMapping("/{id}/like/{userId}")
    public void deleteLike(@PathVariable int id,
                            @PathVariable int userId) {
        service.deleteLikeOfFilm(id, userId);
    }

    @GetMapping("/popular")
    public List<Film> getMostPopular(@RequestParam(defaultValue = "10") @Positive int count) {
        return service.getMostPopular(count);
    }
}
