package ru.yandex.practicum.filmorate.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.model.Review;
import ru.yandex.practicum.filmorate.service.ReviewService;

import javax.validation.constraints.Positive;
import javax.validation.constraints.PositiveOrZero;
import java.util.List;

@RestController
@RequestMapping("/reviews")
@RequiredArgsConstructor
@Validated
public class ReviewController {

    private final ReviewService reviewService;

    @PostMapping
    public Review create(@Validated @RequestBody Review review) {
        return reviewService.add(review);
    }

    @PutMapping
    public Review update(@Validated @RequestBody Review review) {
        return reviewService.update(review);
    }

    @DeleteMapping("/{id}")
    public void delete(@PathVariable int id) {
        reviewService.delete(id);
    }

    @GetMapping("/{id}")
    public Review getById(@PathVariable int id) {
        return reviewService.getById(id);
    }

    @GetMapping
    public List<Review> getByFilmId(@RequestParam(name = "filmId", defaultValue = "0") @PositiveOrZero int filmId,
                                    @RequestParam(name = "count", defaultValue = "10") @Positive int count) {
        return reviewService.getByFilmId(filmId, count);
    }

    @PutMapping("/{id}/like/{userId}")
    public void addLike(@PathVariable int id, @PathVariable int userId) {
        reviewService.addLike(id, userId);
    }

    @DeleteMapping("/{id}/like/{userId}")
    public void deleteLike(@PathVariable int id, @PathVariable int userId) {
        reviewService.deleteLike(id, userId);
    }

    @PutMapping("/{id}/dislike/{userId}")
    public void addDislike(@PathVariable int id, @PathVariable int userId) {
        reviewService.addDislike(id, userId);
    }

    @DeleteMapping("/{id}/dislike/{userId}")
    public void deleteDislike(@PathVariable int id, @PathVariable int userId) {
        reviewService.deleteDislike(id, userId);
    }

}