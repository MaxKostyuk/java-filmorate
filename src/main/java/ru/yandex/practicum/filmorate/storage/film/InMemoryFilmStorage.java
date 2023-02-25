package ru.yandex.practicum.filmorate.storage.film;

import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.enums.SearchBy;
import ru.yandex.practicum.filmorate.model.Director;
import ru.yandex.practicum.filmorate.model.Film;

import java.util.*;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

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
    public List<Film> getByDirector(int directorId, FilmsSortBy sortBy) {
        Comparator<Film> orderByDefault = Comparator.comparingInt(Film::getId);
        Comparator<Film> orderByYear = Comparator.comparing(Film::getReleaseDate);
        Comparator<Film> orderByLikesCount = Comparator.comparingInt(f -> Optional.ofNullable(f.getLikesFromUsers()).orElse(new HashSet<>()).size());

        Comparator<Film> comparator = sortBy == FilmsSortBy.YEAR
                ? orderByYear
                : sortBy == FilmsSortBy.LIKES
                ? orderByLikesCount
                : orderByDefault;

        return filmMap.values().stream()
                .filter((film -> Optional.ofNullable(film.getDirectors()).orElse(new HashSet<>()).stream()
                        .map(Director::getId).collect(Collectors.toSet()).contains(directorId)))
                .sorted(comparator)
                .collect(Collectors.toList());
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
    public List<Film> searchFilms(String query, SearchBy type) {
        List<Film> result = new ArrayList<>();
        switch (type) {
            case BOTH:
                for (int i = 0; i < filmMap.size(); i++) {
                    if (filmMap.get(i).getName().contains(query) ||
                            filmMap.get(i).getDirectors().contains(query)) {
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
                    if (filmMap.get(i).getDirectors().contains(query)) {
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

    @Override
    public List<Film> getMostPopularByGenreAndYear(int count, int genreId, int year) {
        return null;
    }

    @Override
    public List<Film> getCommonFilms(int userId, int friendId) {
        return null;
    }

}
