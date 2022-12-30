package ru.yandex.practicum.filmorate.storage.film;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.exception.ElementNotFoundException;
import ru.yandex.practicum.filmorate.model.Film;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Slf4j
@Component
public class InMemoryFilmStorage  implements FilmStorage {

    private final Map<Integer, Film> filmMap = new HashMap<>();
    private int idCounter = 0;

    @Override
    public Film create(Film film) {
        film.setId(getNextId());
        filmMap.put(film.getId(), film);
        log.info("Film with id " + film.getId() + " was added");
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
        if (!filmMap.containsKey(film.getId()))
            throw new ElementNotFoundException("Film with id " + film.getId() + " not found", film);
        filmMap.put(film.getId(), film);
        log.info("Film with id " + film.getId() + " was updated");
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
