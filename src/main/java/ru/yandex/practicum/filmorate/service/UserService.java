package ru.yandex.practicum.filmorate.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.user.UserStorage;

import java.util.Collection;
import java.util.Set;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class UserService {

    private final UserStorage userStorage;

    public Collection<User> getAllUsers() {
        return userStorage.getAll();
    }


    public User createUser(User user) {
        if (user.getName() == null | user.getName().isBlank())
            user.setName(user.getLogin());
        return userStorage.create(user);
    }

    public User updateUser(User user) {
        if (user.getName() == null | user.getName().isBlank())
            user.setName(user.getLogin());
        return userStorage.update(user);
    }

    public Set<Integer> getUsersFriends(int id) {
        User user = userStorage.getById(id);
        return user.getFriendsList();
    }

    public Set<Integer> getCommonFriends(int id, int otherId) {
        User user1 = userStorage.getById(id);
        User user2 = userStorage.getById(otherId);
        Set<Integer> commonFriends = user1.getFriendsList().stream()
                .filter(element -> user2.getFriendsList().contains(element))
                .collect(Collectors.toSet());
        return commonFriends;
    }

    public void addToFriends(int id, int friendId) {
        User user1 = userStorage.getById(id);
        User user2 = userStorage.getById(friendId);
        Set<Integer> user1Friends = user1.getFriendsList();
        Set<Integer> user2Friends = user2.getFriendsList();
        user1Friends.add(friendId);
        user2Friends.add(id);
        userStorage.update(user1);
        userStorage.update(user2);
    }

    public void deleteFromFriends(int id, int friendId) {
        User user1 = userStorage.getById(id);
        User user2 = userStorage.getById(friendId);
        Set<Integer> user1Friends = user1.getFriendsList();
        Set<Integer> user2Friends = user2.getFriendsList();
        user1Friends.remove(friendId);
        user2Friends.remove(friendId);
        userStorage.update(user1);
        userStorage.update(user2);
    }
}
