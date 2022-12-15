package ru.yandex.practicum.filmorate.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.exception.ElementNotFoundException;
import ru.yandex.practicum.filmorate.model.User;

import javax.validation.Valid;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

@Slf4j
@RestController
@RequestMapping("/users")
public class UserController {

    private final Map<Integer, User> userMap = new HashMap<>();
    private int idCounter = 0;

    @GetMapping
    private Collection<User> getAllUsers() {
        return userMap.values();
    }

    @PostMapping
    private User createUser(@Valid @RequestBody User user) {
        user.setId(setNewId());
        putUserToMap(user, "User with id " + user.getId() + " was added");
        return user;
    }

    @PutMapping
    private User updateUser(@Valid @RequestBody User user) {
        if (!userMap.containsKey(user.getId()))
            throw new ElementNotFoundException("User with id " + user.getId() + " not found", user);
        putUserToMap(user, "User with id " + user.getId() + " was updated");
        return user;
    }

    private void putUserToMap(User user, String message) {
        if (user.getName() == null | user.getName().isBlank())
            user.setName(user.getLogin());
        userMap.put(user.getId(), user);
        log.info(message);
    }

    @ExceptionHandler(ElementNotFoundException.class)
    private ResponseEntity elementNotFoundHandler(ElementNotFoundException e) {
        log.warn(e.getMessage());
        return new ResponseEntity<>(e.getElement(),HttpStatus.NOT_FOUND);
    }

    private int setNewId() {
        return ++idCounter;
    }
}
