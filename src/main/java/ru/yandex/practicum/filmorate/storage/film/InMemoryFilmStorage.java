package ru.yandex.practicum.filmorate.storage.film;

import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.constants.SearchBy;
import ru.yandex.practicum.filmorate.model.Film;

import java.util.*;

@Component
public class InMemoryFilmStorage implements FilmStorage {

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

    @Override
    //TODO: Заменить getDescription на getDirector когда поле появится
    public List<Film> searchFilms(String query, SearchBy type) {
        List<Film> result = new ArrayList<>();
        switch (type) {
            case BOTH:
                for (int i = 0; i < filmMap.size(); i++) {
                    if (filmMap.get(i).getName().contains(query) ||
                            filmMap.get(i).getDescription().contains(query)) {
                        result.add(filmMap.get(i));
                    }
                }
                break;
            case TITLE:
                for (int i = 0; i < filmMap.size(); i++) {
                    if (filmMap.get(i).getName().contains(query)) {
                        result.add(filmMap.get(i));
                    }
                }
                break;
            case DIRECTOR:
                for (int i = 0; i < filmMap.size(); i++) {
                    if (filmMap.get(i).getDescription().contains(query)) {
                        result.add(filmMap.get(i));
                    }
                }
                break;
        }
        return result;
    }

    private int getNextId() {
        return ++idCounter;
    }
}
