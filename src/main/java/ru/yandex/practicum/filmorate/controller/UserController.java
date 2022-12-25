package ru.yandex.practicum.filmorate.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.service.UserService;

import javax.validation.Valid;
import java.util.Collection;
import java.util.Set;

@RestController
@RequestMapping("/users")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;

    @GetMapping
    private Collection<User> getAllUsers() {
        return userService.getAllUsers();
    }

    @PostMapping
    private User createUser(@Valid @RequestBody User user) {
        return userService.createUser(user);
    }

    @PutMapping
    private User updateUser(@Valid @RequestBody User user) {
        return userService.updateUser(user);
    }

    @GetMapping("/{id}/friends")
    private Set<Integer> getUsersFriends(@PathVariable int id) {
        return userService.getUsersFriends(id);
    }

    @GetMapping("/{id}/friends/{otherId}")
    private Set<Integer> getCommonFriends(@PathVariable int id,
                                          @PathVariable int otherId) {
        return userService.getCommonFriends(id, otherId);
    }

    @PutMapping("/{id}/friends/{friendId}")
//    надо наверно установить код возврата
    private void addToFriends(@PathVariable int id,
                                 @PathVariable int friendId) {
        userService.addToFriends(id, friendId);
    }

    @DeleteMapping("/{id}/friends/{friendId}")
    private void deleteFromFriends(@PathVariable int id,
                                 @PathVariable int friendId) {
        userService.deleteFromFriends(id, friendId);
    }
}
