package ru.yandex.practicum.filmorate.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exception.ElementNotFoundException;
import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.storage.genre.GenreStorage;

import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class GenreService {

    private final GenreStorage genreStorage;

    public List<Genre> getAll() {
        return genreStorage.getAll();
    }


    public Genre getById(int id) {
        return genreStorage.getById(id)
                .orElseThrow(() -> new ElementNotFoundException("Genre with id " + id + " not found", id));
    }

    public Genre create(Genre genre) {
        Genre createdGenre = genreStorage.create(genre);
        log.info("Genre with id {} was added", createdGenre.getId());
        return createdGenre;
    }

}
