package ru.yandex.practicum.filmorate.service;

import lombok.RequiredArgsConstructor;
import ru.yandex.practicum.filmorate.storage.review.ReviewStorage;

@RequiredArgsConstructor
public class ReviewService {

    private final ReviewStorage reviewStorage;


}
