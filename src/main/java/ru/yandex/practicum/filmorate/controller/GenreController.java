package ru.yandex.practicum.filmorate.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.service.GenreService;

import javax.validation.Valid;
import java.util.List;

@RestController
@RequestMapping("genres")
@RequiredArgsConstructor
public class GenreController {

    private final GenreService service;

    @GetMapping
    public List<Genre> getAll() {
        return service.getAll();
    }

    @GetMapping("/{id}")
    private Genre getById(@PathVariable int id) {
        return service.getById(id);
    }

    @PostMapping
    private Genre create(@Valid @RequestBody Genre genre) {
        return service.create(genre);
    }
}
