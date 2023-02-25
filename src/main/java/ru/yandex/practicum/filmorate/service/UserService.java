package ru.yandex.practicum.filmorate.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exception.ElementNotFoundException;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.user.UserStorage;

import java.util.*;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class UserService {

    private final UserStorage userStorage;

    public List<User> getAll() {
        return userStorage.getAll();
    }


    public User create(User user) {
        validateName(user);
        User createdUser = userStorage.create(user);
        log.info("User with id {} was added", createdUser.getId());
        return createdUser;
    }

    public User update(User user) {
        validateName(user);
        User userToUpdate = getById(user.getId());
        userToUpdate.setEmail(user.getEmail());
        userToUpdate.setLogin(user.getLogin());
        userToUpdate.setName(user.getName());
        userToUpdate.setBirthday(user.getBirthday());
        userStorage.update(user);
        log.info("User with id {} was updated", user.getId());
        return userToUpdate;
    }

    public Set<User> getFriends(int id) {
        return getById(id).getFriendsList().stream()
                .map(this::getById)
                .collect(Collectors.toCollection(LinkedHashSet::new));
    }

    public Set<User> getCommonFriends(int id, int otherId) {
        User user1 = getById(id);
        User user2 = getById(otherId);
        return user1.getFriendsList().stream()
                .filter(element -> user2.getFriendsList().contains(element))
                .map(this::getById)
                .collect(Collectors.toSet());
    }

    public void addToFriends(int id, int friendId) {
        User user1 = getById(id);
        User user2 = getById(friendId);
        user1.getFriendsList().add(friendId);
        userStorage.update(user1);
        log.info("User with id {} was updated", id);
    }

    public void deleteFromFriends(int id, int friendId) {
        User user1 = getById(id);
        User user2 = getById(friendId);
        user1.getFriendsList().remove(friendId);
        userStorage.update(user1);
        log.info("User with id {} was updated", id);
    }

    public User getById(int id) {
        return userStorage.getById(id)
                .orElseThrow(() -> new ElementNotFoundException("User with id " + id + " not found", id));
    }

    private void validateName(User user) {
        if (user.getName() == null | user.getName().isBlank())
            user.setName(user.getLogin());
    }
}
