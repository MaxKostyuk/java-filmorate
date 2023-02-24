package ru.yandex.practicum.filmorate;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.DirtiesContext;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.user.UserDbStorage;

import java.time.LocalDate;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertTrue;

@SpringBootTest
@AutoConfigureTestDatabase
@RequiredArgsConstructor(onConstructor_ = @Autowired)
@DirtiesContext(classMode = DirtiesContext.ClassMode.BEFORE_EACH_TEST_METHOD)
public class UserDbStorageTest {

    private final UserDbStorage userDbStorage;

/*    @BeforeEach
    public void beforeEach() {
        User user = new User(1, "example@gmail.com", "login", LocalDate.now());
        userDbStorage.create(user);
    }*/

    @Test
    public void readUserTest() {
        Optional<User> user = userDbStorage.getById(1);
        assertTrue(user.isPresent());
        assertTrue(user.get().getId() == 1);
    }

//    @Test
//    public void createUserTest() {
//        User user = new User(2, "other@gmail.com", "otherLogin", LocalDate.now());
//        userDbStorage.create(user);
//        Optional<User> userFromDb = userDbStorage.getById(2);
//        assertTrue(userFromDb.isPresent());
//        assertTrue(user.equals(userFromDb.get()));
//    }

/*    @Test
    public void updateUserTest() {
        User user = new User(1, "other@gmail.com", "otherLogin", LocalDate.now());
        userDbStorage.update(user);
        Optional<User> userFromDb = userDbStorage.getById(1);
        assertTrue(userFromDb.isPresent());
        assertTrue(user.equals(userFromDb.get()));
    }*/

/*    @Test
    public void getAllUsersTest() {
        User user = new User(2, "other@gmail.com", "otherLogin", LocalDate.now());
        userDbStorage.create(user);
        assertTrue(userDbStorage.getAll().size() == 2);
    }*/

/*    @Test
    public void deleteUserTest() {
        User user = new User(2, "other@gmail.com", "otherLogin", LocalDate.now());
        userDbStorage.create(user);
        assertTrue(userDbStorage.getAll().size() == 2);
        userDbStorage.delete(1);
        assertTrue(userDbStorage.getAll().size() == 1);
        userDbStorage.delete(2);
        assertTrue(userDbStorage.getAll().size() == 0);
    }*/
}
