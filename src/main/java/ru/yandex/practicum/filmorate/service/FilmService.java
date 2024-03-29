package ru.yandex.practicum.filmorate.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exception.ElementNotFoundException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.film.FilmStorage;
import ru.yandex.practicum.filmorate.storage.genre.GenreStorage;
import ru.yandex.practicum.filmorate.storage.rating.RatingStorage;
import ru.yandex.practicum.filmorate.storage.user.UserStorage;

import java.util.*;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class FilmService {

    private final FilmStorage filmStorage;
    private final UserStorage userStorage;
    private final RatingStorage ratingStorage;
    private final GenreStorage genreStorage;

    public List<Film> getAllFilms() {
        return filmStorage.getAll();
    }

    public Film createFilm(Film film) {
        validateRating(film);
        validateGenres(film);
        Film createdFilm = filmStorage.create(film);
        log.info("Film with id {} was added", createdFilm.getId());
        return createdFilm;
    }

    public Film updateFilm(Film film) {
        validateRating(film);
        validateGenres(film);
        Film filmToUpdate = getById(film.getId());
        filmToUpdate.setName(film.getName());
        filmToUpdate.setDescription(film.getDescription());
        filmToUpdate.setReleaseDate(film.getReleaseDate());
        filmToUpdate.setDuration(film.getDuration());
        filmToUpdate.setMpa(film.getMpa());
        filmToUpdate.setGenres(film.getGenres());
        filmStorage.update(filmToUpdate);
        log.info("Film with id {} was updated", film.getId());
        return filmToUpdate;
    }

    public void addLikeToFilm(int id, int userId) {
        User user = userStorage.getById(userId)
                .orElseThrow(() -> new ElementNotFoundException("User with id " + id + " not found", id));
        Film film = getById(id);
        film.getLikesFromUsers().add(userId);
        filmStorage.update(film);
        log.info("Film with id {} was updated", film.getId());
    }

    public void deleteLikeOfFilm(int id, int userId) {
        User user = userStorage.getById(userId)
                .orElseThrow(() -> new ElementNotFoundException("User with id " + id + " not found", id));
        Film film = getById(id);
        film.getLikesFromUsers().remove(userId);
        filmStorage.update(film);
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

    private void validateRating(Film film) {
        film.setMpa(ratingStorage.getById(film.getMpa().getId())
                .orElseThrow(() ->
                        new ElementNotFoundException("Rating with id " + film.getMpa().getId() + " not found", film.getMpa().getId())));
    }

    private void validateGenres(Film film) {
        if (film.getGenres() == null) {
            film.setGenres(new LinkedHashSet<>());
        } else {
            List<Genre> filledList = new ArrayList<>();
            for(Genre filmGenre : film.getGenres()) {
                filledList.add(genreStorage.getById(filmGenre.getId())
                        .orElseThrow(() ->
                            new ElementNotFoundException("Genre with id " + filmGenre.getId() + " not found", filmGenre.getId())));
            }
            film.setGenres(filledList.stream()
                            .sorted(Comparator.comparingInt(Genre::getId))
                            .collect(Collectors.toCollection(LinkedHashSet::new)));
        }
    }
}
