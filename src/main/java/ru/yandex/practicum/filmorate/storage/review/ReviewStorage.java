package ru.yandex.practicum.filmorate.storage.review;

import ru.yandex.practicum.filmorate.model.Review;

import java.util.List;
import java.util.Optional;

public interface ReviewStorage {
    Review create(Review review);

    Optional<Review> getById(int id);

    List<Review> getByFilmId(int filmId, int count);

    List<Review> getAllReviews(int count);

    Review update(Review review);

    void delete(int id);

    void addLike(int reviewId, int userId);

    void deleteLike(int reviewId, int userId);

    void addDislike(int reviewId, int userId);

    void deleteDislike(int reviewId, int userId);

}
