package ru.yandex.practicum.filmorate.storage.film;

import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.ResultSetExtractor;
import ru.yandex.practicum.filmorate.model.Director;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.model.Rating;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class FilmsMapper implements ResultSetExtractor<List<Film>> {
    @Override
    public List<Film> extractData(ResultSet resultSet) throws SQLException, DataAccessException {
        Map<Long, Film> films = new LinkedHashMap<>();
        while (resultSet.next()) {
            Long id = resultSet.getLong("film_id");
            Film film = films.get(id);
            if (film == null) {
                Rating rating = new Rating();
                rating.setId(resultSet.getInt("rating_id"));
                rating.setName(resultSet.getString("rating"));

                film = new Film(resultSet.getInt("film_id"), resultSet.getString("name"),
                        resultSet.getString("description"), resultSet.getDate("releaseDate").toLocalDate(),
                        resultSet.getInt("duration"), rating, new HashSet<>(), new HashSet<>());
                films.put(id, film);
            }

            int genreId = resultSet.getInt("genre_id");
            if (!resultSet.wasNull()) {
                String name = resultSet.getString("genre");
                Genre genre = new Genre();
                genre.setId(genreId);
                genre.setName(name);
                film.getGenres().add(genre);
            }

            int directorId = resultSet.getInt("director_id");
            if(!resultSet.wasNull()) {
                String name = resultSet.getString("director");
                Director director = Director.builder().id(directorId).name(name).build();
                film.getDirectors().add(director);
            }
        }
        return new ArrayList<>(films.values());
    }
}
