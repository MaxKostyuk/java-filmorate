package ru.yandex.practicum.filmorate.storage.film;

import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.model.Film;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Component
public class InMemoryFilmStorage  implements FilmStorage {

    private final Map<Integer, Film> filmMap = new HashMap<>();
    private int idCounter = 0;

    @Override
    public Film create(Film film) {
        film.setId(getNextId());
        filmMap.put(film.getId(), film);
        return film;
    }

    @Override
    public List<Film> getAll() {
        return List.copyOf(filmMap.values());
    }

    @Override
    public Optional<Film> getById(int id) {
            return Optional.ofNullable(filmMap.get(id));
    }

    @Override
    public Film update(Film film) {
        filmMap.put(film.getId(), film);
        return film;
    }

    @Override
    public void delete(int id) {
        filmMap.remove(id);
    }

    private int getNextId() {
        return ++idCounter;
    }
}
