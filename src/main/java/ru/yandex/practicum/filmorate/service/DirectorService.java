package ru.yandex.practicum.filmorate.service;

import lombok.RequiredArgsConstructor;
import org.springframework.dao.IncorrectResultSizeDataAccessException;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exception.ElementNotFoundException;
import ru.yandex.practicum.filmorate.model.Director;
import ru.yandex.practicum.filmorate.storage.director.DirectorStorage;

import java.util.List;

@Service
@RequiredArgsConstructor
public class DirectorService {
    private final String DIRECTOR_NOT_EXISTS_TEMPLATE = "A director with this id: %d not exists.";
    private final DirectorStorage storage;

    public Director create(Director director) {
        int id = storage.create(director);
        String name = director.getName();
        return Director.builder().id(id).name(name).build();
    }

    public Director update(Director director) {
        int id = director.getId();
        int updatedRowsCount = storage.update(director);
        if(updatedRowsCount == 0) {
            throw new ElementNotFoundException(String.format(DIRECTOR_NOT_EXISTS_TEMPLATE, id), director);
        }
        String name = director.getName();
        return Director.builder().id(id).name(name).build();
    }

    public Director get(int id) {
        try {
            return storage.get(id);
        } catch (IncorrectResultSizeDataAccessException ex) {
            throw new ElementNotFoundException(String.format(DIRECTOR_NOT_EXISTS_TEMPLATE, id), id);
        }
    }

    public List<Director> get() {
        return storage.get();
    }

    public Director delete(int id) {
        try {
            Director director = storage.get(id);
            storage.delete(id);
            return director;
        } catch (IncorrectResultSizeDataAccessException ex) {
            throw new ElementNotFoundException(String.format(DIRECTOR_NOT_EXISTS_TEMPLATE, id), id);
        }
    }
}
