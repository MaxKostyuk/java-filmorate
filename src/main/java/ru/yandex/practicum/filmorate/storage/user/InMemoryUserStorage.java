package ru.yandex.practicum.filmorate.storage.user;

import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.model.UserEvent;

import java.util.*;

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
    public List<User> getAll() {
        return List.copyOf(userMap.values());
    }

    @Override
    public Optional<User> getById(int id) {
        return Optional.ofNullable(userMap.get(id));
    }

    @Override
    public User update(User user) {
        userMap.put(user.getId(), user);
        return user;
    }

    @Override
    public void delete(int id) {
        userMap.remove(id);
    }

    @Override
    public List<UserEvent> getUserEvents(int userId) {
        return null;
    }

    private int getNextId() {
        return ++idCounter;
    }
}
