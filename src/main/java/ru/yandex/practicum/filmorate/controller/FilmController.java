package ru.yandex.practicum.filmorate.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.service.FilmService;
import ru.yandex.practicum.filmorate.storage.film.FilmsSortBy;

import javax.validation.Valid;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.Positive;
import java.util.List;
import java.util.Optional;

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

    @GetMapping("/director/{directorId}")
    public List<Film> getDirectorFilms(@PathVariable int directorId, @RequestParam(defaultValue = "year") FilmsSortBy sortBy) {
        return service.getDirectorFilms(directorId, sortBy);
    }

    @GetMapping("/search")
    public List<Film> searchFilms(@RequestParam("query") @NotBlank @NotEmpty String query,
                                  @RequestParam("by") List<String> by) {
        return service.searchFilms(query, by);
    }
    
    //Метод удаления фильма по ИД
    @DeleteMapping("/{id}")
    public void deleteById(@PathVariable int id) {
        service.deleteById(id);
    }

    //Метод возвращает наиболее популярные фильмы с возможностью фильтрации по году и жанру
    @GetMapping("/popular")
    public List<Film> getMostPopularByGenreAndYear(@RequestParam(name = "count", defaultValue = "10") @Positive int count,
                                                   @RequestParam(name = "genreId", required = false) Optional<Integer> genreId,
                                                   @RequestParam(name = "year", required = false) Optional<Integer> year) {

        if (genreId.isPresent() && genreId.get() > 0 && year.isPresent() && year.get() > 1894) {
            return service.getMostPopularByGenreAndYear(count, genreId.get(), year.get());
        } else if (genreId.isPresent() && genreId.get() > 0 && year.isEmpty()) {
            return service.getMostPopularByGenreAndYear(count, genreId.get(), -1);
        } else if (genreId.isEmpty() && year.isPresent() && year.get() > 1894) {
            return service.getMostPopularByGenreAndYear(count, -1, year.get());
        } else {
            //авторский метод сохранил как и обсуждали
            return service.getMostPopular(count);
        }
    }
}
