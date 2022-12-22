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
    public User create(User user) {
        user.setId(getNextId());
        userMap.put(user.getId(), user);
        return user;
    }

    @Override
    public Collection<User> getAll() {
        return userMap.values();
    }

    @Override
    public User getById(int id) {
        return userMap.get(id);
    }

    @Override
    public User update(User user) {
        if (!userMap.containsKey(user.getId()))
            throw new ElementNotFoundException("User with id " + user.getId() + " not found", user);
        if (user.getName() == null | user.getName().isBlank())
            user.setName(user.getLogin());
        return user;
    }

    @Override
    public void delete(int id) {
        userMap.remove(id);
    }

    private int getNextId() {
        return ++idCounter;
    }
}
