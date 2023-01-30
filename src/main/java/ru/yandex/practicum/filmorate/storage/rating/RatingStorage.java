package ru.yandex.practicum.filmorate.storage.rating;

import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.model.Rating;

import java.util.List;
import java.util.Optional;

@Component
public interface RatingStorage {

    Rating create(Rating rating);

    List<Rating> getAll();
    Optional<Rating> getById(int id);

    Rating update(Rating rating);

    void delete(int id);
}

