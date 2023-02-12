package ru.yandex.practicum.filmorate.storage.genre;

import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Primary;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.filmorate.model.Genre;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;
import java.util.Optional;


@Repository
@Primary
@RequiredArgsConstructor
public class GenreDbStorage implements GenreStorage {
    private final JdbcTemplate jdbcTemplate;

    @Override
    public Genre create(Genre genre) {
        String sqlQuery = "insert into genre(GENRE_NAME) values (?)";
        KeyHolder keyHolder = new GeneratedKeyHolder();
        jdbcTemplate.update(connection -> {
            PreparedStatement stmt = connection.prepareStatement(sqlQuery, new String[]{"genre_id"});
            stmt.setString(1, genre.getName());
            return stmt;
        }, keyHolder);
        return jdbcTemplate.query("SELECT * FROM GENRE WHERE GENRE_ID = ?", new GenreMapper(), keyHolder.getKey().longValue())
                .stream().findFirst().orElse(null);
    }

    @Override
    public List<Genre> getAll() {
        return jdbcTemplate.query("SELECT * FROM genre ORDER BY GENRE_ID ASC", new GenreMapper());
    }

    @Override
    public Optional<Genre> getById(int id) {
        return jdbcTemplate.query("SELECT * FROM GENRE WHERE GENRE_ID = ?", new GenreMapper(), id)
                .stream().findFirst();
    }

    @Override
    public Genre update(Genre genre) {
        jdbcTemplate.update("UPDATE genre SET genre_name = ? WHERE genre_id = ?", genre.getName(), genre.getId());
        return jdbcTemplate.query("SELECT * FROM genre WHERE genre_id = ?", new GenreMapper(), genre.getId())
                .stream().findFirst().orElse(null);
    }

    @Override
    public void delete(int id) {
        jdbcTemplate.update("DELETE FROM GENRE WHERE GENRE_ID = ?", id);
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
}
