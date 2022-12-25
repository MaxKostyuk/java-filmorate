package ru.yandex.practicum.filmorate.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.storage.film.FilmStorage;

import java.util.Collection;
import java.util.HashSet;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class FilmService {

    private final FilmStorage filmStorage;

    public Collection<Film> getAllFilms() {
        return filmStorage.getAll();
    }

    public Film createFilm(Film film) {
        if (film.getLikesFromUsers() == null) {
            film.setLikesFromUsers(new HashSet<>());
        }
        return filmStorage.create(film);
    }

    public Film updateFilm(Film film) {
        if (film.getLikesFromUsers() == null) {
            film.setLikesFromUsers(new HashSet<>());
        }
        return filmStorage.update(film);
    }

    public void addLikeToFilm(int id, int userId) {
        Film film = filmStorage.getById(id);
        film.getLikesFromUsers().add(userId);
        filmStorage.update(film);
    }

    public void deleteLikeOfFilm(int id, int userId) {
        Film film = filmStorage.getById(id);
        film.getLikesFromUsers().remove(userId);
        filmStorage.update(film);
    }

    public Collection<Film> getMostPopular(int size) {
        return filmStorage.getAll()
                .stream()
                .sorted((f0,f1) -> f0.getLikesFromUsers().size() - f1.getLikesFromUsers().size())
                .limit(size)
                .collect(Collectors.toList());
    }
}
