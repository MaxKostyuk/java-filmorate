package ru.yandex.practicum.filmorate.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exception.ElementNotFoundException;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.user.UserStorage;

import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class UserService {

    private final UserStorage userStorage;

    public List<User> getAllUsers() {
        return userStorage.getAll();
    }


    public User createUser(User user) {
        validateName(user);
        log.info("User with id " + user.getId() + " was added");
        return userStorage.create(user);
    }

    public User updateUser(User user) {
        validateName(user);
        log.info("User with id " + user.getId() + " was updated");
        return userStorage.update(user);
    }

    public Set<User> getUsersFriends(int id) {
        User user = getById(id);
        Set<User> friendsList = new LinkedHashSet<>();
        for (int friendsId : user.getFriendsList()) {
            friendsList.add(getById(friendsId));
        }
        return friendsList;
    }

    public Set<User> getCommonFriends(int id, int otherId) {
        User user1 = getById(id);
        User user2 = getById(otherId);
        Set<Integer> commonFriendsIds = user1.getFriendsList().stream()
                .filter(element -> user2.getFriendsList().contains(element))
                .collect(Collectors.toSet());
        Set<User> commonFriends = new LinkedHashSet<>();
        for (int friendsId : commonFriendsIds) {
            commonFriends.add(getById(friendsId));
        }
        return commonFriends;
    }

    public void addToFriends(int id, int friendId) {
        User user1 = getById(id);
        User user2 = getById(friendId);
        Set<Integer> user1Friends = user1.getFriendsList();
        Set<Integer> user2Friends = user2.getFriendsList();
        user1Friends.add(friendId);
        user2Friends.add(id);
        userStorage.update(user1);
        userStorage.update(user2);
    }

    public void deleteFromFriends(int id, int friendId) {
        User user1 = getById(id);
        User user2 = getById(friendId);
        Set<Integer> user1Friends = user1.getFriendsList();
        Set<Integer> user2Friends = user2.getFriendsList();
        user1Friends.remove(friendId);
        user2Friends.remove(friendId);
        userStorage.update(user1);
        userStorage.update(user2);
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
