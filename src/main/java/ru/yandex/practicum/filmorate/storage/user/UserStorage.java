package ru.yandex.practicum.filmorate.storage.user;

import ru.yandex.practicum.filmorate.model.User;

import java.util.Collection;

public interface UserStorage {

    User create(User user);

    Collection<User> getAll();
    User getById(int id);

    User update(User user);

    void delete(int id);
}
