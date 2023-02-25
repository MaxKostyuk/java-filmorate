package ru.yandex.practicum.filmorate.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import ru.yandex.practicum.filmorate.model.Director;
import ru.yandex.practicum.filmorate.service.DirectorService;

import javax.validation.Valid;
import java.util.List;

@RestController
@RequestMapping("directors")
@RequiredArgsConstructor
@Slf4j
public class DirectorController {

    public static final String LOGGING_GET_DIRECTOR_BY_ID_TEMPLATE = "GET '/directors/%s'";
    public static final String LOGGING_GET_DIRECTORS_TEMPLATE = "GET '/directors'";
    public static final String LOGGING_POST_DIRECTOR_TEMPLATE = "POST '/directors', parameters={%s}";
    public static final String LOGGING_PUT_DIRECTOR_TEMPLATE = "PUT '/directors', parameters={%s}";
    public static final String LOGGING_DELETE_DIRECTOR_TEMPLATE = "DELETE '/directors/%s'";

    private final DirectorService directorService;

    @GetMapping("/{id}")
    public Director get(@PathVariable int id) {
        log.info(String.format(LOGGING_GET_DIRECTOR_BY_ID_TEMPLATE, id));
        return directorService.get(id);
    }

    @GetMapping
    public List<Director> get() {
        log.info(String.format(LOGGING_GET_DIRECTORS_TEMPLATE));
        return directorService.get();
    }

    @PostMapping
    public Director create(@Valid @RequestBody Director director) {
        log.info(String.format(LOGGING_POST_DIRECTOR_TEMPLATE, director));
        return directorService.create(director);
    }

    @PutMapping
    public Director update(@Valid @RequestBody Director director) {
        log.info(String.format(LOGGING_PUT_DIRECTOR_TEMPLATE, director));
        return directorService.update(director);
    }

    @DeleteMapping("/{id}")
    public Director delete(@PathVariable int id) {
        log.info(String.format(LOGGING_DELETE_DIRECTOR_TEMPLATE, id));
        return directorService.delete(id);
    }
}