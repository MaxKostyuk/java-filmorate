package ru.yandex.practicum.filmorate.storage.director;

import ru.yandex.practicum.filmorate.model.Director;

import java.util.List;

public interface DirectorStorage {
    Director get(int id);

    List<Director> get();

    int create(Director director);

    int update(Director director);

    int delete(int id);

}
