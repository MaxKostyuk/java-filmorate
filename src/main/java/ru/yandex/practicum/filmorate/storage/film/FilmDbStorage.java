package ru.yandex.practicum.filmorate.storage.film;

import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Primary;
import org.springframework.jdbc.core.BatchPreparedStatementSetter;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.filmorate.enums.SearchBy;
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
public class    FilmDbStorage implements FilmStorage {

    /*
      Константа POWER_OF_RELATIONSHIP определяет максимальное количество наиболее схожих пользователей
      лайки которых используются для формирования рекомендаций по просмотру фильмов
      Цель ограничения: если оставить слишком много схожих пользователей, то полученная выборка
      рекомендованных фильмов может быть огромна и выбрать из нее что-то крайне сложно,
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
        saveGenres(film);
        saveDirectors(film);
        return getById(keyHolder.getKey().intValue()).get();
    }

    @Override
    public List<Film> getAll() {

        String sql = "SELECT f.film_id, f.name, f.description, f.releaseDate, f.duration, " +
                "f.rating_id, r.rating_name AS rating, " +
                "fg.genre_id, g.genre_name AS genre, " +
                "fd.director_id, d.name AS director " +
                "FROM film AS f " +
                "LEFT JOIN rating AS r " +
                "ON f.rating_id = r.rating_id " +
                "LEFT JOIN film_genres fg " +
                "ON f.film_id = fg.film_id " +
                "LEFT JOIN genre AS g " +
                "ON fg.genre_id = g.genre_id " +
                "LEFT JOIN film_directors AS fd " +
                "ON f.film_id = fd.film_id " +
                "LEFT JOIN director AS d " +
                "ON fd.director_id = d.director_id;";

        return jdbcTemplate.query(sql, new FilmsMapper());
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
        String sql = "SELECT f.film_id, f.name, f.description, f.releaseDate, f.duration, " +
                "f.rating_id, r.rating_name AS rating, " +
                "fg.genre_id, g.genre_name AS genre, " +
                "fd.director_id, d.name AS director " +
                "FROM film AS f " +
                "LEFT JOIN rating AS r " +
                "ON f.rating_id = r.rating_id " +
                "LEFT JOIN film_genres fg " +
                "ON f.film_id = fg.film_id " +
                "LEFT JOIN genre AS g " +
                "ON fg.genre_id = g.genre_id " +
                "LEFT JOIN film_directors AS fd " +
                "ON f.film_id = fd.film_id " +
                "LEFT JOIN director AS d " +
                "ON fd.director_id = d.director_id " +
                "WHERE f.film_id = ?";

        Optional<Film> createdFilm = jdbcTemplate.query(sql, new FilmsMapper(), id).stream().findFirst();
        if (createdFilm.isPresent())
            createdFilm.get().setLikesFromUsers(getLikes(createdFilm.get()));
        return createdFilm;
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
        Set<Genre> newGenresList = Optional.ofNullable(film.getGenres()).orElse(new HashSet<>());
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
        Set<Integer> newLikesList = Optional.ofNullable(film.getLikesFromUsers()).orElse(new HashSet<>());
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

    private void saveGenres(Film film) {
        String sqlInsertFilmGenres = "INSERT INTO film_genres (film_id, genre_id) VALUES ";
        int filmId = film.getId();
        Set<Genre> genres = Optional.ofNullable(film.getGenres()).orElse(new HashSet<>());
        if (genres.size() > 0) {
            List<Integer> genresId = genres.stream().map(Genre::getId).collect(Collectors.toList());
            String genresString = genresId.stream().map(genreId -> String.format("( %d, %d )", filmId, genreId)).collect(Collectors.joining(", "));
            jdbcTemplate.update(sqlInsertFilmGenres + genresString);
        }
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

    @Override
    public List<Film> searchFilms(String query, SearchBy type) {
        String sql;
        switch (type) {
            case BOTH:
                sql = "SELECT f.film_id, f.name, f.description, f.releaseDate, f.duration, " +
                        "f.rating_id, r.rating_name AS rating, " +
                        "fg.genre_id, g.genre_name AS genre, " +
                        "fd.director_id, d.name AS director " +
                        "FROM film AS f JOIN rating AS r ON f.rating_id = r.rating_id " +
                        "JOIN film_directors AS fd ON f.film_id = fd.film_id " +
                        "JOIN director AS d ON fd.director_id = d.director_id " +
                        "LEFT JOIN film_genres fg " +
                        "ON f.film_id = fg.film_id " +
                        "LEFT JOIN genre AS g " +
                        "ON fg.genre_id = g.genre_id " +
                        "WHERE (d.name ILIKE '%"+query+"%') " +
                        "UNION " +
                        "SELECT f.film_id, f.name, f.description, f.releaseDate, f.duration, " +
                        "f.rating_id, r.rating_name AS rating, " +
                        "fg.genre_id, g.genre_name AS genre, " +
                        "fd.director_id, d.name AS director " +
                        "FROM film AS f JOIN rating AS r ON f.rating_id = r.rating_id " +
                        "LEFT JOIN film_genres fg " +
                        "ON f.film_id = fg.film_id " +
                        "LEFT JOIN genre AS g " +
                        "ON fg.genre_id = g.genre_id " +
                        "LEFT JOIN film_directors AS fd " +
                        "ON f.film_id = fd.film_id " +
                        "LEFT JOIN director AS d " +
                        "ON fd.director_id = d.director_id " +
                        "WHERE (f.name ILIKE '%"+query+"%') " +
                        "ORDER BY FILM_ID DESC;";
                return jdbcTemplate.query(sql, new FilmsMapper());
            //System.out.println(result);
            //break;
            case TITLE:
                sql = "SELECT f.film_id, f.name, f.description, f.releaseDate, f.duration, " +
                        "f.rating_id, r.rating_name AS rating, " +
                        "fg.genre_id, g.genre_name AS genre, " +
                        "fd.director_id, d.name AS director " +
                        "FROM film AS f JOIN rating AS r ON f.rating_id = r.rating_id " +
                        "LEFT JOIN film_genres fg " +
                        "ON f.film_id = fg.film_id " +
                        "LEFT JOIN genre AS g " +
                        "ON fg.genre_id = g.genre_id " +
                        "LEFT JOIN film_directors AS fd " +
                        "ON f.film_id = fd.film_id " +
                        "LEFT JOIN director AS d " +
                        "ON fd.director_id = d.director_id " +
                        "WHERE (f.name ILIKE '%"+query+"%') " +
                        "ORDER BY FILM_ID DESC;";;
                return jdbcTemplate.query(sql, new FilmsMapper());
            case DIRECTOR:
                sql = "SELECT f.film_id, f.name, f.description, f.releaseDate, f.duration, " +
                        "f.rating_id, r.rating_name AS rating, " +
                        "fg.genre_id, g.genre_name AS genre, " +
                        "fd.director_id, d.name AS director " +
                        "FROM film AS f JOIN rating AS r ON f.rating_id = r.rating_id " +
                        "JOIN film_directors AS fd ON f.film_id = fd.film_id " +
                        "JOIN director AS d ON fd.director_id = d.director_id " +
                        "LEFT JOIN film_genres fg " +
                        "ON f.film_id = fg.film_id " +
                        "LEFT JOIN genre AS g " +
                        "ON fg.genre_id = g.genre_id " +
                        "WHERE (d.name ILIKE '%"+query+"%');";
                return jdbcTemplate.query(sql, new FilmsMapper());
        }
        return new ArrayList<>();
    }

    //Метод формирует список фильмов рекомендованных к просмотру для пользователя с id указанным в userId
    public List<Film> getRecommendations(int userId) {
        /*
          Получаем из БД перечень фильмов которые посмотрели пользователи имеющие схожие интересы (то есть лайкали
          те же фильмы, что и пользователь, которому нужна рекомендация, но при этом так же лайкали и фильмы,
          которые не смотрел данный пользователь.
         */
        String sqlRecommendation = "SELECT DISTINCT f.film_id, f.name, f.description, f.releaseDate, f.duration, " +
                "       f.rating_id, r.rating_name as rating, " +
                "       fg.genre_id, g.genre_name as genre, " +
                "       fd.director_id, d.name as director " +
                "FROM film AS f " +
                "LEFT JOIN rating AS r " +
                "ON f.rating_id = r.rating_id " +
                "INNER JOIN film_likes AS fl " +
                "ON f.film_id = fl.film_id " +
                "INNER JOIN (SELECT sf.user_id AS common_users FROM film_likes AS sf " +
                "            INNER JOIN film_likes as uf " +
                "            ON sf.film_id = uf.film_id " +
                "            AND sf.user_id <> ? " +
                "            GROUP BY sf.user_id " +
                "            ORDER BY COUNT(sf.film_id) DESC " +
                "            LIMIT ?) AS rf " +
                "ON fl.user_id = rf.common_users " +
                "LEFT OUTER JOIN (SELECT film_id AS films_to_remove FROM film_likes WHERE user_id = ?) AS rf " +
                "ON f.film_id = rf.films_to_remove " +
                "LEFT JOIN film_likes AS ef " +
                "ON fl.film_id = ef.film_id " +
                "LEFT JOIN film_directors AS fd " +
                "ON f.film_id = fd.film_id " +
                "LEFT JOIN director AS d " +
                "ON d.director_id = fd.director_id " +
                "LEFT JOIN film_genres fg " +
                "ON f.film_id = fg.film_id " +
                "LEFT JOIN genre AS g " +
                "ON fg.genre_id = g.genre_id " +
                "WHERE rf.films_to_remove IS NULL;";

        return jdbcTemplate.query(sqlRecommendation, new FilmsMapper(), userId, POWER_OF_RELATIONSHIP, userId);

    }

    //Метод возвращает количество count самых популярных фильмов жанра genreId вышедших в году year
    public List<Film> getMostPopularByGenreAndYear(int count, int genreId, int year) {

        String sqlMostPopularByGenreAndYear = "SELECT f.film_id, f.name, f.description, f.releaseDate, f.duration, " +
                "       f.rating_id, r.rating_name AS rating, " +
                "       fg.genre_id, g.genre_name AS genre, " +
                "       fd.director_id, d.name AS director, " +
                "       ff.likes " +
                "FROM film AS f " +
                "INNER JOIN (SELECT tf.film_id, bf.likes " +
                "            FROM film AS tf " +
                "            LEFT OUTER JOIN (SELECT film_id, COUNT(user_id) AS likes " +
                "                             FROM film_likes " +
                "                             GROUP BY film_id) as bf " +
                "            ON tf.film_id = bf.film_id " +
                "            LEFT JOIN film_genres AS fgs " +
                "            ON tf.film_id = fgs.film_id " +
                "            WHERE fgs.genre_id = ? " +
                "            AND EXTRACT(YEAR FROM tf.releaseDate) = ? " +
                "            ORDER BY bf.likes DESC " +
                "            LIMIT ?) AS ff " +
                "ON f.film_id = ff.film_id " +
                "LEFT JOIN rating AS r " +
                "ON f.rating_id = r.rating_id " +
                "LEFT JOIN film_genres fg " +
                "ON f.film_id = fg.film_id " +
                "LEFT JOIN genre AS g " +
                "ON fg.genre_id = g.genre_id " +
                "LEFT JOIN film_directors AS fd " +
                "ON f.film_id = fd.film_id " +
                "LEFT JOIN director AS d ON d.director_id = fd.director_id;";


        String sqlMostPopularByGenre = "SELECT f.film_id, f.name, f.description, f.releaseDate, f.duration, " +
                "f.rating_id, r.rating_name AS rating, " +
                "fg.genre_id, g.genre_name AS genre, " +
                "fd.director_id, d.name AS director, " +
                "ff.likes " +
                "FROM film AS f " +
                "INNER JOIN (SELECT tf.film_id, bf.likes " +
                "            FROM film AS tf " +
                "            LEFT OUTER JOIN (SELECT film_id, COUNT(user_id) AS likes " +
                "                             FROM film_likes " +
                "                             GROUP BY film_id) as bf " +
                "            ON tf.film_id = bf.film_id " +
                "            LEFT JOIN film_genres AS fgs " +
                "            ON tf.film_id = fgs.film_id " +
                "            WHERE fgs.genre_id = ? " +
                "            ORDER BY bf.likes DESC " +
                "            LIMIT ?) AS ff " +
                "ON f.film_id = ff.film_id " +
                "LEFT JOIN rating AS r " +
                "ON f.rating_id = r.rating_id " +
                "LEFT JOIN film_genres fg " +
                "ON f.film_id = fg.film_id " +
                "LEFT JOIN genre AS g " +
                "ON fg.genre_id = g.genre_id " +
                "LEFT JOIN film_directors AS fd " +
                "ON f.film_id = fd.film_id " +
                "LEFT JOIN director AS d ON d.director_id = fd.director_id;";


        String sqlMostPopularByYear = "SELECT f.film_id, f.name, f.description, f.releaseDate, f.duration, " +
                "       f.rating_id, r.rating_name AS rating, " +
                "       fg.genre_id, g.genre_name AS genre, " +
                "       fd.director_id, d.name AS director, " +
                "       ff.likes " +
                "FROM film AS f " +
                "INNER JOIN (SELECT tf.film_id, bf.likes " +
                "            FROM film AS tf " +
                "            LEFT OUTER JOIN (SELECT film_id, COUNT(user_id) AS likes " +
                "                             FROM film_likes " +
                "                             GROUP BY film_id) as bf " +
                "            ON tf.film_id = bf.film_id " +
                "            WHERE EXTRACT(YEAR FROM tf.releaseDate) = ? " +
                "            ORDER BY bf.likes DESC " +
                "            LIMIT ?) AS ff " +
                "ON f.film_id = ff.film_id " +
                "LEFT JOIN rating AS r " +
                "ON f.rating_id = r.rating_id " +
                "LEFT JOIN film_genres fg " +
                "ON f.film_id = fg.film_id " +
                "LEFT JOIN genre AS g " +
                "ON fg.genre_id = g.genre_id " +
                "LEFT JOIN film_directors AS fd " +
                "ON f.film_id = fd.film_id " +
                "LEFT JOIN director AS d ON d.director_id = fd.director_id;";

        String sqlMostPopular = "SELECT f.film_id, f.name, f.description, f.releaseDate, f.duration, " +
                "       f.rating_id, r.rating_name AS rating, " +
                "       fg.genre_id, g.genre_name AS genre, " +
                "       fd.director_id, d.name AS director, " +
                "       ff.likes " +
                "FROM film AS f " +
                "INNER JOIN (SELECT tf.film_id, bf.likes " +
                "            FROM film AS tf " +
                "            LEFT OUTER JOIN (SELECT film_id, COUNT(user_id) AS likes " +
                "                             FROM film_likes " +
                "                             GROUP BY film_id) as bf " +
                "            ON tf.film_id = bf.film_id " +
                "            ORDER BY bf.likes DESC " +
                "            LIMIT ?) AS ff " +
                "ON f.film_id = ff.film_id " +
                "LEFT JOIN rating AS r " +
                "ON f.rating_id = r.rating_id " +
                "LEFT JOIN film_genres fg " +
                "ON f.film_id = fg.film_id " +
                "LEFT JOIN genre AS g " +
                "ON fg.genre_id = g.genre_id " +
                "LEFT JOIN film_directors AS fd " +
                "ON f.film_id = fd.film_id " +
                "LEFT JOIN director AS d ON d.director_id = fd.director_id;";


        if (genreId == -1 && year == -1) {
            return jdbcTemplate.query(sqlMostPopular, new FilmsMapper(), count);
        } else if (genreId == -1) {
            return jdbcTemplate.query(sqlMostPopularByYear, new FilmsMapper(), year, count);
        } else if (year == -1) {
            return jdbcTemplate.query(sqlMostPopularByGenre, new FilmsMapper(), genreId, count);
        } else {
            return jdbcTemplate.query(sqlMostPopularByGenreAndYear, new FilmsMapper(), genreId, year, count);
        }
    }

    //Метод возвращает общие фильмы двух пользователей
    public List<Film> getCommonFilms(int userId, int friendId) {

        String sqlCommonFilms = "SELECT f.film_id, f.name, f.description, f.releaseDate, f.duration, " +
                "f.rating_id, r.rating_name as rating, " +
                "fg.genre_id, g.genre_name as genre, " +
                "fd.director_id, d.name as director " +
                "FROM film AS f " +
                "LEFT JOIN rating AS r " +
                "ON f.rating_id = r.rating_id " +
                "LEFT JOIN film_likes AS fl " +
                "ON f.film_id = fl.film_id " +
                "INNER JOIN (SELECT sf.user_id AS common_users FROM film_likes AS sf " +
                "            INNER JOIN film_likes as uf " +
                "            ON sf.film_id = uf.film_id " +
                "            AND uf.user_id = ? " +
                "            AND sf.user_id <> ? " +
                "            GROUP BY sf.user_id " +
                "            ORDER BY COUNT(sf.film_id) DESC " +
                "            LIMIT ?) AS rf " +
                "ON fl.user_id = rf.common_users AND common_users = ? " +
                "JOIN film_likes AS ef " +
                "ON fl.film_id = ef.film_id AND ef.user_id = ? " +
                "LEFT JOIN film_directors AS fd " +
                "ON f.film_id = fd.film_id " +
                "LEFT JOIN director AS d " +
                "ON d.director_id = fd.director_id " +
                "LEFT JOIN film_genres fg " +
                "ON f.film_id = fg.film_id " +
                "LEFT JOIN genre AS g " +
                "ON fg.genre_id = g.genre_id;";

        return jdbcTemplate.query(sqlCommonFilms, new FilmsMapper(), userId, userId, POWER_OF_RELATIONSHIP, friendId , userId);
    }
}
