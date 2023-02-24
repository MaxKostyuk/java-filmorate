package ru.yandex.practicum.filmorate;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.DirtiesContext;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.Rating;
import ru.yandex.practicum.filmorate.storage.film.FilmDbStorage;

import java.time.LocalDate;
import java.util.HashSet;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertTrue;

@SpringBootTest
@AutoConfigureTestDatabase
@RequiredArgsConstructor(onConstructor_ = @Autowired)
@DirtiesContext(classMode = DirtiesContext.ClassMode.BEFORE_EACH_TEST_METHOD)
public class FilmDbStorageTest {

    private final FilmDbStorage filmDbStorage;

   /* @BeforeEach
    public void beforeEach() {
        Rating rating = new Rating();
        rating.setId(1);
        Film film = new Film(1, "name", "description", LocalDate.now(), 100, rating, null, new HashSet<>());
        filmDbStorage.create(film);
    }

    @Test
    public void createFilmTest() {
        Rating rating = new Rating();
        rating.setId(2);
        rating.setName("PG");
        Film film = new Film(2, "otherName", "otherDescription", LocalDate.now(), 100, rating, new HashSet<>(), new HashSet<>());
        filmDbStorage.create(film);
        Optional<Film> filmFromDb = filmDbStorage.getById(2);
        assertTrue(filmFromDb.isPresent());
        assertTrue(film.equals(filmFromDb.get()));
    }

    @Test
    public void readFilmTest() {
        Rating rating = new Rating();
        rating.setId(1);
        rating.setName("G");
        Film film = new Film(1, "name", "description", LocalDate.now(), 100, rating, null, new HashSet<>());
        Optional<Film> filmFromDb = filmDbStorage.getById(1);
        assertTrue(filmFromDb.isPresent());
        assertTrue(film.equals(filmFromDb.get()));
    }

    @Test
    public void updateFilmTest() {
        Rating rating = new Rating();
        rating.setId(2);
        rating.setName("PG");
        Film film = new Film(1, "otherName", "newDescription", LocalDate.now(), 100, rating, null, new HashSet<>());
        filmDbStorage.update(film);
        Optional<Film> filmFromDb = filmDbStorage.getById(1);
        assertTrue(filmFromDb.isPresent());
        assertTrue(film.equals(filmFromDb.get()));
    }

    @Test
    public void deleteFilmTest() {
        assertTrue(filmDbStorage.getAll().size() == 1);
        filmDbStorage.delete(1);
        assertTrue(filmDbStorage.getAll().size() == 0);
    }

    @Test
    public void getAllFilmsTest() {
        Rating rating = new Rating();
        rating.setId(2);
        rating.setName("PG");
        Film film = new Film(1, "otherName", "newDescription", LocalDate.now(), 100, rating, null, new HashSet<>());
        filmDbStorage.create(film);
        assertTrue(filmDbStorage.getAll().size() == 2);
    }*/
}
