package ru.yandex.practicum.filmorate.storage.film;

import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.model.Film;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

@Component
public class InMemoryFilmStorage  implements FilmStorage {

    private final Map<Integer, Film> filmMap = new HashMap<>();
    private int idCounter = 0;

    @Override
    public Collection<Film> getAllFilms() {
        return filmMap.values();
    }

    @Override
    public Film createFilm(Film film) {
        film.setId(getNextId());
        filmMap.put(film.getId(), film);
        return film;
    }

    @Override
    public Film updateFilm(Film film) {
        return null;
    }

    private int getNextId() {
        return ++idCounter;
    }
}
