package ru.yandex.practicum.filmorate.storage.user;

import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.exception.ElementNotFoundException;
import ru.yandex.practicum.filmorate.model.User;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

@Component
public class InMemoryUserStorage implements UserStorage {

    private final Map<Integer, User> userMap = new HashMap<>();
    private int idCounter = 0;

    @Override
    public Collection<User> getAllUsers() {
        return userMap.values();
    }

    @Override
    public User createUser(User user) {
        user.setId(getNextId());
        if (user.getName() == null | user.getName().isBlank())
            user.setName(user.getLogin());
        userMap.put(user.getId(), user);
        return user;
    }

    @Override
    public User updateUser(User user) {
        if (!userMap.containsKey(user.getId()))
            throw new ElementNotFoundException("User with id " + user.getId() + " not found", user);
        if (user.getName() == null | user.getName().isBlank())
            user.setName(user.getLogin());
        return user;
    }

    private int getNextId() {
        return ++idCounter;
    }
}
