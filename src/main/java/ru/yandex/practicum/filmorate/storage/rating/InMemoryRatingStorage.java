package ru.yandex.practicum.filmorate.storage.rating;

import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.model.Rating;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Component
public class InMemoryRatingStorage implements RatingStorage {

    private final Map<Integer, Rating> ratingMap = new HashMap<>();
    private int idCounter = 0;

    @Override
    public Rating create(Rating rating) {
        rating.setId(getNextId());
        ratingMap.put(rating.getId(), rating);
        return rating;
    }

    @Override
    public List<Rating> getAll() {
        return List.copyOf(ratingMap.values());
    }

    @Override
    public Optional<Rating> getById(int id) {
        return Optional.ofNullable(ratingMap.get(id));
    }

    @Override
    public Rating update(Rating rating) {
        ratingMap.put(rating.getId(), rating);
        return rating;
    }

    @Override
    public void delete(int id) {
        ratingMap.remove(id);
    }

    private int getNextId() {
        return ++idCounter;
    }
}
