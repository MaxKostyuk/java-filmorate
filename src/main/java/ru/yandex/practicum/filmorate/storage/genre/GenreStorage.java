package ru.yandex.practicum.filmorate.storage.genre;

import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.model.Genre;

import java.util.List;
import java.util.Optional;

@Component
public interface GenreStorage {

    Genre create(Genre genre);

    List<Genre> getAll();
    Optional<Genre> getById(int id);

    Genre update(Genre genre);

    void delete(int id);
}
