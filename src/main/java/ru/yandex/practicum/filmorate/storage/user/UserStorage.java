package ru.yandex.practicum.filmorate.storage.user;

import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.model.UserEvent;

import java.util.List;
import java.util.Optional;

public interface UserStorage {

    User create(User user);

    List<User> getAll();
    Optional<User> getById(int id);

    User update(User user);

    void delete(int id);

    List<UserEvent> getUserEvents(int userId);

    void addFriendEvent(int userId, int friendId);
}
