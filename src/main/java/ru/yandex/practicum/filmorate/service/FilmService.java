package ru.yandex.practicum.filmorate.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exception.ElementNotFoundException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.film.FilmStorage;
import ru.yandex.practicum.filmorate.storage.user.UserStorage;

import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class FilmService {

    private final FilmStorage filmStorage;
    private final UserStorage userStorage;

    public List<Film> getAllFilms() {
        return filmStorage.getAll();
    }

    public Film createFilm(Film film) {
        log.info("Film with id {} was added", film.getId());
        return filmStorage.create(film);
    }

    public Film updateFilm(Film film) {
        Film filmToUpdate = getById(film.getId());
        filmToUpdate.setName(film.getName());
        filmToUpdate.setDescription(film.getDescription());
        filmToUpdate.setReleaseDate(film.getReleaseDate());
        filmToUpdate.setDuration(film.getDuration());
        log.info("Film with id {} was updated", film.getId());
        return filmToUpdate;
    }

    public void addLikeToFilm(int id, int userId) {
        User user = userStorage.getById(userId)
                .orElseThrow(() -> new ElementNotFoundException("User with id " + id + " not found", id));
        Film film = getById(id);
        film.getLikesFromUsers().add(userId);
        log.info("Film with id {} was updated", film.getId());
    }

    public void deleteLikeOfFilm(int id, int userId) {
        User user = userStorage.getById(userId)
                .orElseThrow(() -> new ElementNotFoundException("User with id " + id + " not found", id));
        Film film = getById(id);
        film.getLikesFromUsers().remove(userId);
        log.info("Film with id {} was updated", film.getId());
    }

    public List<Film> getMostPopular(int size) {
        return filmStorage.getAll()
                .stream()
                .sorted((f0, f1) -> f1.getLikesFromUsers().size() - f0.getLikesFromUsers().size())
                .limit(size)
                .collect(Collectors.toList());
    }

    public Film getById(int id) {
        return filmStorage.getById(id)
                .orElseThrow(() -> new ElementNotFoundException("Film with id " + id + " not found", id));
    }
}
