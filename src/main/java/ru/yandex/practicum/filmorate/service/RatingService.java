package ru.yandex.practicum.filmorate.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exception.ElementNotFoundException;
import ru.yandex.practicum.filmorate.model.Rating;
import ru.yandex.practicum.filmorate.storage.rating.RatingStorage;

import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class RatingService {

    private final RatingStorage ratingStorage;

    public List<Rating> getAll() {
        return ratingStorage.getAll();
    }


    public Rating getById(int id) {
        return ratingStorage.getById(id)
                .orElseThrow(() -> new ElementNotFoundException("Rating with id " + id + " not found", id));
    }

    public Rating create(Rating rating) {
        Rating createdRating = ratingStorage.create(rating);
        log.info("Rating with id {} was added", createdRating.getId());
        return createdRating;
    }

}
