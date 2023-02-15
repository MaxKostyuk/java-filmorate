package ru.yandex.practicum.filmorate.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.service.UserService;
import ru.yandex.practicum.filmorate.model.Film;

import javax.validation.Valid;
import java.util.List;
import java.util.Set;

@RestController
@RequestMapping("/users")
@RequiredArgsConstructor
public class UserController {

    private final UserService service;

    @GetMapping
    private List<User> getAll() {
        return service.getAll();
    }

    @PostMapping
    private User create(@Valid @RequestBody User user) {
        return service.create(user);
    }

    @PutMapping
    private User update(@Valid @RequestBody User user) {
        return service.update(user);
    }

    @GetMapping("/{id}")
    private User getById(@PathVariable int id) {
        return service.getById(id);
    }

    @GetMapping("/{id}/friends")
    private Set<User> getFriends(@PathVariable int id) {
        return service.getFriends(id);
    }

    @GetMapping("/{id}/friends/common/{otherId}")
    private Set<User> getCommonFriends(@PathVariable int id,
                                          @PathVariable int otherId) {
        return service.getCommonFriends(id, otherId);
    }

    @PutMapping("/{id}/friends/{friendId}")
    private void addToFriends(@PathVariable int id,
                                 @PathVariable int friendId) {
        service.addToFriends(id, friendId);
    }

    @DeleteMapping("/{id}/friends/{friendId}")
    private void deleteFromFriends(@PathVariable int id,
                                 @PathVariable int friendId) {
        service.deleteFromFriends(id, friendId);
    }

    //Метод возвращает фильмы рекомендованные для просмотра
    @GetMapping("/{id}/recommendations")
    private List<Film> getRecommendations(@PathVariable int id) {
        return service.getRecommendations(id);
    }
}
