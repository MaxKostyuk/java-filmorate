package ru.yandex.practicum.filmorate.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.exception.ElementNotFoundException;
import ru.yandex.practicum.filmorate.model.Film;

import javax.validation.Valid;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

@Slf4j
@RestController
@RequestMapping("films")
public class FilmController {

    private final Map<Integer, Film> filmMap = new HashMap<>();
    private int idCounter = 0;

    @GetMapping
    public Collection<Film> getAllFilms() {
        return filmMap.values();
    }

    @PostMapping
    private Film createFilm(@Valid @RequestBody Film film) {
        film.setId(setNewId());
        putFilmToMap(film, "Film with id " + film.getId() + " was added");
        return film;
    }

    @PutMapping
    private Film updateFilm(@Valid @RequestBody Film film) {
        if (!filmMap.containsKey(film.getId()))
            throw new ElementNotFoundException("Film with id " + film.getId() + " not found", film);
        putFilmToMap(film, "Film with id " + film.getId() + " was updated");
        return film;
    }

    private void putFilmToMap(Film film, String message) {
        filmMap.put(film.getId(), film);
        log.info(message);
    }

    private int setNewId() {
        return ++idCounter;
    }
}
