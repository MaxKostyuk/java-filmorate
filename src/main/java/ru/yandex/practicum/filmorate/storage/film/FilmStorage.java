package ru.yandex.practicum.filmorate.storage.film;

import ru.yandex.practicum.filmorate.model.Film;

import java.util.List;
import java.util.Optional;

public interface FilmStorage {

    Film create(Film film);

    List<Film> getAll();
    Optional<Film> getById(int id);
    List<Film> getByDirector(int directorId, FilmsSortBy sortBy);

    Film update(Film film);

    void delete(int id);
}
