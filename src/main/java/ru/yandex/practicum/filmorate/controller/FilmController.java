package ru.yandex.practicum.filmorate.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
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
@RequiredArgsConstructor
public class FilmController {

    private final FilmService service;

    @GetMapping
    public List<Film> getAll() {
        return service.getAllFilms();
    }

    @PostMapping
    private Film create(@Valid @RequestBody Film film) {
        return service.createFilm(film);
    }

    @PutMapping
    private Film update(@Valid @RequestBody Film film) {
        return service.updateFilm(film);
    }

    @GetMapping("/{id}")
    private Film getById(@PathVariable int id) {
        return service.getFilmById(id);
    }

    @PutMapping("/{id}/like/{userId}")
    private void addLike(@PathVariable int id,
                         @PathVariable int userId) {
        service.addLikeToFilm(id, userId);
    }

    @DeleteMapping("/{id}/like/{userId}")
    private void deleteLike(@PathVariable int id,
                            @PathVariable int userId) {
        service.deleteLikeOfFilm(id, userId);
    }


    @Validated
    @GetMapping("/popular")
    private List<Film> getMostPopular(@RequestParam(defaultValue = "10") @Positive int count) {
        return service.getMostPopular(count);
    }

    @ExceptionHandler
    public ResponseEntity<String> illegalArgumentExceptionHandler(IllegalArgumentException e){
        log.warn("Invalid argument " + e.getMessage());
        return new ResponseEntity<>("Invalid argument " + e.getMessage(), HttpStatus.BAD_REQUEST);
    }
}
