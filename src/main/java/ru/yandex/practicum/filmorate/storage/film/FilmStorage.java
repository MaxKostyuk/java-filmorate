package ru.yandex.practicum.filmorate.storage.film;

import ru.yandex.practicum.filmorate.model.Film;

import java.util.Collection;

public interface FilmStorage {

    Film create(Film film);

    Collection<Film> getAll();
    Film getById(int id);

    Film update(Film film);

    void delete(int id);
}
