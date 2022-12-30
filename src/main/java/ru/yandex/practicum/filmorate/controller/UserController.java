package ru.yandex.practicum.filmorate.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.service.UserService;

import javax.validation.Valid;
import java.util.List;
import java.util.Set;

@RestController
@RequestMapping("/users")
@RequiredArgsConstructor
public class UserController {

    private final UserService service;

    @GetMapping
    private List<User> getAllUsers() {
        return service.getAllUsers();
    }

    @PostMapping
    private User createUser(@Valid @RequestBody User user) {
        return service.createUser(user);
    }

    @PutMapping
    private User updateUser(@Valid @RequestBody User user) {
        return service.updateUser(user);
    }

    @GetMapping("/{id}")
    private User getUserById(@PathVariable int id) {
        return service.getById(id);
    }

    @GetMapping("/{id}/friends")
    private Set<User> getUsersFriends(@PathVariable int id) {
        return service.getUsersFriends(id);
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
}
