package ru.yandex.practicum.filmorate.storage.user;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.exception.ElementNotFoundException;
import ru.yandex.practicum.filmorate.model.User;

import java.util.*;

@Slf4j
@Component
public class InMemoryUserStorage implements UserStorage {

    private final Map<Integer, User> userMap = new HashMap<>();
    private int idCounter = 0;

    @Override
    public User create(User user) {
        user.setId(getNextId());
        if (user.getFriendsList() == null) {
            user.setFriendsList(new HashSet<>());
        }
        userMap.put(user.getId(), user);
        return user;
    }

    @Override
    public List<User> getAll() {
        return List.copyOf(userMap.values());
    }

    @Override
    public Optional<User> getById(int id) {
        return Optional.of(userMap.get(id));
    }

    @Override
    public User update(User user) {
        if (!userMap.containsKey(user.getId()))
            throw new ElementNotFoundException("User with id " + user.getId() + " not found", user);
        if (user.getName() == null | user.getName().isBlank())
            user.setName(user.getLogin());
        if (user.getFriendsList() == null) {
            user.setFriendsList(new HashSet<>());
        }
        userMap.put(user.getId(), user);
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
