package ru.yandex.practicum.filmorate.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exception.ElementNotFoundException;
import ru.yandex.practicum.filmorate.model.Review;
import ru.yandex.practicum.filmorate.model.UserEvent;
import ru.yandex.practicum.filmorate.storage.event.EventStorage;
import ru.yandex.practicum.filmorate.storage.film.FilmStorage;
import ru.yandex.practicum.filmorate.storage.review.ReviewStorage;
import ru.yandex.practicum.filmorate.storage.user.UserStorage;

import java.util.List;

@Slf4j
@RequiredArgsConstructor
@Service
public class ReviewService {

    private final ReviewStorage reviewStorage;
    private final UserStorage userStorage;
    private final FilmStorage filmStorage;
    private final EventStorage eventStorage;


    public Review add(Review review) {
        validateUserId(review.getUserId());
        validateFilmId(review.getFilmId());
        Review createdReview = reviewStorage.create(review);
        eventStorage.createEvent(new UserEvent(
                "REVIEW",
                "ADD",
                review.getUserId(),
                createdReview.getReviewId()
        ));
        log.info("Review with id {} was added", createdReview.getReviewId());
        return createdReview;
    }


    public Review update(Review review) {
        Review updatedReview = reviewStorage.update(review);
        eventStorage.createEvent(new UserEvent(
                "REVIEW",
                "UPDATE",
                updatedReview.getUserId(),
                updatedReview.getReviewId()
        ));
        log.info("Review with id {} was updated", updatedReview.getReviewId());
        return updatedReview;
    }


    public void delete(int id) {
        Review review = getById(id);
        eventStorage.createEvent(new UserEvent(
                "REVIEW",
                "REMOVE",
                review.getUserId(),
                id
        ));
        reviewStorage.delete(id);
    }


    public Review getById(int id) {
        return reviewStorage.getById(id).orElseThrow(
                () -> new ElementNotFoundException("Review with id " + id + " not found", id));
    }


    public List<Review> getByFilmId(int filmId, int count) {
        if(filmId != 0)
            return reviewStorage.getByFilmId(filmId, count);
        else
            return reviewStorage.getAllReviews(count);
    }



    private void validateFilmId(int id) {
        filmStorage.getById(id)
                .orElseThrow(() -> new ElementNotFoundException("Film with id " + id + " not found", id));
    }

    private void validateUserId(int id) {
        userStorage.getById(id)
                .orElseThrow(() -> new ElementNotFoundException("User with id " + id + " not found", id));
    }

    private void validateReviewId(int id) {
        reviewStorage.getById(id)
                .orElseThrow(() -> new ElementNotFoundException("Review with id " + id + " not found", id));
    }

    public void addLike(int reviewId, int userId) {
        validateReviewId(reviewId);
        validateUserId(userId);
        reviewStorage.addLike(reviewId, userId);
    }

    public void deleteLike(int reviewId, int userId) {
        reviewStorage.deleteLike(reviewId, userId);
    }

    public void addDislike(int reviewId, int userId) {
        validateReviewId(reviewId);
        validateUserId(userId);
        reviewStorage.addDislike(reviewId, userId);
    }

    public void deleteDislike(int reviewId, int userId) {
        reviewStorage.deleteDislike(reviewId, userId);
    }

}
