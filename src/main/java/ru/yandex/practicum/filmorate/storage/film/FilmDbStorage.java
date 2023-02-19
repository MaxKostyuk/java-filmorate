package ru.yandex.practicum.filmorate.storage.film;

import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Primary;
import org.springframework.jdbc.core.BatchPreparedStatementSetter;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.filmorate.model.Director;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.model.Rating;
import ru.yandex.practicum.filmorate.storage.director.DirectorMapper;

import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

@Repository
@RequiredArgsConstructor
@Primary
public class FilmDbStorage implements FilmStorage {

    /*
      Константа POWER_OF_RELATIONSHIP определяет максимальное количество наиболее схожих пользователей
      лайки которых используются для формирования рекомендаций по просмотру фильмов
      Цель ограничения: если оставить слишком много схожих пользователей, то полученная выборка
      рекомендованных фильмов может быть огромно и выбрать из нее что-то крайне сложно,
      более того без ограничения в крайнем случае может сложиться ситуация когда рекомендация к просмотру
      может содержать вообще все фильмы.
     */
    private static final int POWER_OF_RELATIONSHIP = 10;

    private final JdbcTemplate jdbcTemplate;


    @Override
    public Film create(Film film) {
        String sqlQuery = "insert into FILM(NAME, DESCRIPTION, RELEASEDATE, DURATION, RATING_ID) values (?,?,?,?,?)";
        KeyHolder keyHolder = new GeneratedKeyHolder();
        jdbcTemplate.update(connection -> {
            PreparedStatement stmt = connection.prepareStatement(sqlQuery, new String[]{"film_id"});
            stmt.setString(1, film.getName());
            stmt.setString(2, film.getDescription());
            stmt.setDate(3, Date.valueOf(film.getReleaseDate()));
            stmt.setInt(4, film.getDuration());
            stmt.setInt(5, film.getMpa().getId());
            return stmt;
        }, keyHolder);
        film.setId(keyHolder.getKey().intValue());
        for (Genre genre : film.getGenres()) {
            jdbcTemplate.update("INSERT INTO FILM_GENRES (FILM_ID, GENRE_ID) VALUES (?, ?)", film.getId(), genre.getId());
        }
        saveDirectors(film);
        return getById(keyHolder.getKey().intValue()).get();
    }

    @Override
    public List<Film> getAll() {
        String sql = "SELECT * FROM FILM AS F JOIN RATING AS R ON F.RATING_ID = R.RATING_ID";
        List<Film> films = jdbcTemplate.query(sql, new FilmMapper());
        for (Film film : films) {
            film.setGenres(getGenres(film));
            film.setLikesFromUsers(getLikes(film));
            film.setDirectors(getDirectors(film));
        }
        return films;
    }

    @Override
    public List<Film> getByDirector(int directorId, FilmsSortBy sortBy) {
        String sql = "SELECT f.film_id, f.name, f.description, f.releaseDate, f.duration, " +
                "f.rating_id, r.rating_name as rating, " +
                "fg.genre_id, g.genre_name as genre, " +
                "fd.director_id, d.name as director, " +
                "fl.likes " +
                "FROM film as f " +
                "LEFT JOIN rating as r " +
                "ON f.rating_id = r.rating_id " +
                "LEFT JOIN film_genres fg " +
                "ON f.film_id = fg.film_id " +
                "LEFT JOIN genre as g " +
                "ON fg.genre_id = g.genre_id " +
                "LEFT JOIN film_directors as fd " +
                "ON f.film_id = fd.film_id " +
                "INNER JOIN (SELECT director_id, name FROM director WHERE director_id = ?) as d " +
                "ON fd.director_id = d.director_id " +
                "LEFT JOIN (SELECT film_id, COUNT(user_id) as likes FROM film_likes GROUP BY film_id) as fl " +
                "ON f.film_id = fl.film_id " +
                "ORDER BY ";

        return jdbcTemplate.query(sql + sortBy.getFieldName(), new FilmsMapper(), directorId);
    }

    @Override
    public Optional<Film> getById(int id) {
        String sql = "SELECT * FROM FILM AS F JOIN RATING AS R ON F.RATING_ID = R.RATING_ID WHERE FILM_ID = ?";
        Optional<Film>  film = jdbcTemplate.query(sql, new FilmMapper(), id).stream().findFirst();
        if (film.isPresent()) {
            film.get().setGenres(getGenres(film.get()));
            film.get().setLikesFromUsers(getLikes(film.get()));
            film.get().setDirectors(getDirectors(film.get()));
        }
        return film;
    }

    @Override
    public Film update(Film film) {
        String sql = "UPDATE FILM SET name = ?, description = ?, releaseDate = ?, duration = ?, rating_id = ? WHERE FILM_ID = ?";
        jdbcTemplate.update(sql,film.getName(), film.getDescription(), film.getReleaseDate(), film.getDuration(), film.getMpa().getId(), film.getId());
        updateLikes(film);
        updateGenres(film);
        updateDirectors(film);
        return getById(film.getId()).get();
    }

    @Override
    public void delete(int id) {
        jdbcTemplate.update("DELETE FROM FILM WHERE FILM_ID = ?", id);
    }

    private Set<Genre> getGenres(Film film) {
        String sql = "SELECT fg.GENRE_ID, g.GENRE_NAME FROM FILM_GENRES AS fg JOIN GENRE AS g ON fg.GENRE_ID = g.GENRE_ID WHERE fg.FILM_ID = ?";
        return jdbcTemplate.query(sql, new GenreMapper(), film.getId()).stream().collect(Collectors.toSet());
    }

    private Set<Integer> getLikes(Film film) {
        String sql = "SELECT USER_ID FROM FILM_LIKES WHERE FILM_ID = ?";
        return jdbcTemplate.query(sql, new LikesMapper(), film.getId()).stream().collect(Collectors.toSet());
    }

    private Set<Director> getDirectors(Film film) {
        String sql = "SELECT d.director_id, d.name FROM director as d INNER JOIN film_directors as fd ON fd.director_id = d.director_id AND fd.film_id = ?";
        return new HashSet<>(jdbcTemplate.query(sql, new DirectorMapper(), film.getId()));
    }

    private void updateGenres(Film film) {
        Set<Genre> newGenresList = film.getGenres();
        Set<Genre> oldGenresList = getGenres(film);
        Set<Genre> genresToAdd = new HashSet<>(newGenresList);
        genresToAdd.removeAll(oldGenresList);
        Set<Genre> genresToDelete = new HashSet<>(oldGenresList);
        genresToDelete.removeAll(newGenresList);
        ArrayList<Genre> genresToDeleteAsList = new ArrayList<>(genresToDelete);
        if (!genresToDeleteAsList.isEmpty()) {
            String deleteSql = "DELETE FROM FILM_GENRES WHERE GENRE_ID = ? AND FILM_ID = ?";
            jdbcTemplate.batchUpdate(deleteSql, new BatchPreparedStatementSetter() {
                @Override
                public void setValues(PreparedStatement ps, int i) throws SQLException {
                    ps.setInt(1, genresToDeleteAsList.get(i).getId());
                    ps.setInt(2, film.getId());
                }

                @Override
                public int getBatchSize() {
                    return genresToDeleteAsList.size();
                }
            });
        }
        ArrayList<Genre> genresToAddAsList = new ArrayList<>(genresToAdd);
        if (!genresToAddAsList.isEmpty()) {
            String addSql = "INSERT INTO FILM_GENRES (FILM_ID, GENRE_ID) VALUES (?, ?)";
            jdbcTemplate.batchUpdate(addSql, new BatchPreparedStatementSetter() {
                @Override
                public void setValues(PreparedStatement ps, int i) throws SQLException {
                    ps.setInt(1, film.getId());
                    ps.setInt(2, genresToAddAsList.get(i).getId());
                }

                @Override
                public int getBatchSize() {
                    return genresToAddAsList.size();
                }
            });
        }
    }

    private void updateLikes(Film film) {
        Set<Integer> newLikesList = film.getLikesFromUsers();
        Set<Integer> oldLikesList = getLikes(film);
        Set<Integer> likesToAdd = new HashSet<>(newLikesList);
        likesToAdd.removeAll(oldLikesList);
        Set<Integer> likesToDelete = new HashSet<>(oldLikesList);
        likesToDelete.removeAll(newLikesList);
        ArrayList<Integer> likesToDeleteAsList = new ArrayList<>(likesToDelete);
        if(!likesToDeleteAsList.isEmpty()) {
            jdbcTemplate.batchUpdate("DELETE FROM FILM_LIKES WHERE USER_ID = ? AND FILM_ID = ?", new BatchPreparedStatementSetter() {
                @Override
                public void setValues(PreparedStatement ps, int i) throws SQLException {
                    ps.setInt(1, likesToDeleteAsList.get(i));
                    ps.setInt(2, film.getId());
                }

                @Override
                public int getBatchSize() {
                    return likesToDeleteAsList.size();
                }
            });
        }
        ArrayList<Integer> likesToAddAsList = new ArrayList<>(likesToAdd);
        if(!likesToAddAsList.isEmpty()) {
            jdbcTemplate.batchUpdate("INSERT INTO FILM_LIKES (FILM_ID, USER_ID) VALUES (?, ?)", new BatchPreparedStatementSetter() {
                @Override
                public void setValues(PreparedStatement ps, int i) throws SQLException {
                    ps.setInt(1, film.getId());
                    ps.setInt(2, likesToAddAsList.get(i));
                }

                @Override
                public int getBatchSize() {
                    return likesToAddAsList.size();
                }
            });
        }
    }

    private void saveDirectors(Film film) {
        String sqlInsertFilmDirectors = "INSERT INTO film_directors (film_id, director_id) VALUES ";
        int filmId = film.getId();
        Set<Director> directors = Optional.ofNullable(film.getDirectors()).orElse(new HashSet<>());
        if (directors.size() > 0) {
            List<Integer> directorsId = directors.stream().map(Director::getId).collect(Collectors.toList());
            String genres = directorsId.stream().map(directorId -> String.format("( %d, %d )", filmId, directorId)).collect(Collectors.joining(", "));
            jdbcTemplate.update(sqlInsertFilmDirectors + genres);
        }
    }

    private void deleteDirectors(Film film) {
        String sqlDeleteFilmDirectors = "DELETE FROM film_directors WHERE film_id = ?";
        int filmId = film.getId();
        jdbcTemplate.update(sqlDeleteFilmDirectors, filmId);
    }

    private void updateDirectors(Film film) {
        deleteDirectors(film);
        saveDirectors(film);
    }

    private class FilmMapper implements RowMapper<Film> {


        @Override
        public Film mapRow(ResultSet rs, int rowNum) throws SQLException {
            Film film = new Film();
            Rating rating = new Rating();

            film.setId(rs.getInt("film_id"));
            film.setName(rs.getString("name"));
            film.setDescription(rs.getString("description"));
            film.setReleaseDate(rs.getDate("releaseDate").toLocalDate());
            film.setDuration(rs.getInt("duration"));
            rating.setName(rs.getString("rating_name"));
            rating.setId(rs.getInt("rating_id"));
            film.setMpa(rating);

            return film;
        }
    }
    private class GenreMapper implements RowMapper<Genre> {

        @Override
        public Genre mapRow(ResultSet rs, int rowNum) throws SQLException {
            Genre genre = new Genre();

            genre.setId(rs.getInt("genre_id"));
            genre.setName(rs.getString("genre_name"));

            return genre;
        }
    }

    private class LikesMapper implements RowMapper<Integer> {
        @Override
        public Integer mapRow(ResultSet rs, int rowNum) throws SQLException {
            return rs.getInt("user_id");
        }
    }

    //Метод формирует список фильмов рекомендованных к просмотру для пользователя с id указанным в userId
    public List<Film> getRecommendations(int userId) {
        /*
          Получаем из БД перечень фильмов которые посмотрели пользователи имеющие схожие интересы (то есть лайкали
          те же фильмы, что и пользователь, которому нужна рекомендация, но при этом так же лайкали и фильмы,
          которые не смотрел данный пользователь.
         */
        String sql = "SELECT * " +
                "FROM FILM AS F " +
                "JOIN RATING AS R ON F.RATING_ID = R.RATING_ID " +
                "WHERE F.FILM_ID IN ( SELECT DISTINCT FILM_ID " +
                "                     FROM FILM_LIKES " +
                "                     WHERE USER_ID IN (SELECT USER_ID " +
                "                                       FROM (SELECT DISTINCT USER_ID, COUNT(FILM_ID) AS SIMULARITY " +
                "                                             FROM FILM_LIKES " +
                "                                             WHERE FILM_ID IN (SELECT FILM_ID " +
                "                                                               FROM FILM_LIKES " +
                "                                                               WHERE USER_ID = ?) " +
                "                                             AND USER_ID <> ? " +
                "                                             GROUP BY USER_ID " +
                "                                             ORDER BY SIMULARITY DESC " +
                "                                             LIMIT ?)) " +
                "                     AND FILM_ID NOT IN (SELECT FILM_ID " +
                "                                         FROM FILM_LIKES " +
                "                                         WHERE USER_ID = ?));";

        List<Film> films = jdbcTemplate.query(sql, new FilmMapper(),  userId, userId, POWER_OF_RELATIONSHIP, userId);
        for (Film film : films) {
            film.setGenres(getGenres(film));
            film.setLikesFromUsers(getLikes(film));
            film.setDirectors(getDirectors(film));
        }
        return films;
    }

}
