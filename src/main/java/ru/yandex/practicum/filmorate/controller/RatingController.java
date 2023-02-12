package ru.yandex.practicum.filmorate.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.model.Rating;
import ru.yandex.practicum.filmorate.service.RatingService;

import javax.validation.Valid;
import java.util.List;

@RestController
@RequestMapping("mpa")
@RequiredArgsConstructor
public class RatingController {

    private final RatingService service;

    @GetMapping
    public List<Rating> getAll() {
        return service.getAll();
    }

    @GetMapping("/{id}")
    private Rating getById(@PathVariable int id) {
        return service.getById(id);
    }

    @PostMapping
    private Rating create(@Valid @RequestBody Rating rating) {
        return service.create(rating);
    }
}
