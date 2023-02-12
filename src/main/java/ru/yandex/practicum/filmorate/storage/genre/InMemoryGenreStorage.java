package ru.yandex.practicum.filmorate.storage.genre;

import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.model.Genre;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Component
public class InMemoryGenreStorage implements GenreStorage {

    private final Map<Integer, Genre> genreMap = new HashMap<>();
    private int idCounter = 0;

    @Override
    public Genre create(Genre genre) {
        genre.setId(getNextId());
        genreMap.put(genre.getId(), genre);
        return genre;
    }

    @Override
    public List<Genre> getAll() {
        return List.copyOf(genreMap.values());
    }

    @Override
    public Optional<Genre> getById(int id) {
        return Optional.ofNullable(genreMap.get(id));
    }

    @Override
    public Genre update(Genre genre) {
        genreMap.put(genre.getId(), genre);
        return genre;
    }

    @Override
    public void delete(int id) {
        genreMap.remove(id);
    }

    private int getNextId() {
        return ++idCounter;
    }
}
