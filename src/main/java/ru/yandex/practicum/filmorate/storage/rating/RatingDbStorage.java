package ru.yandex.practicum.filmorate.storage.rating;

import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Primary;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.filmorate.model.Rating;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;
import java.util.Optional;

@Repository
@Primary
@RequiredArgsConstructor
public class RatingDbStorage implements RatingStorage {

    private final JdbcTemplate jdbcTemplate;

    @Override
    public Rating create(Rating rating) {
        String sqlQuery = "insert into rating(RATING_NAME) values (?)";
        KeyHolder keyHolder = new GeneratedKeyHolder();
        jdbcTemplate.update(connection -> {
            PreparedStatement stmt = connection.prepareStatement(sqlQuery, new String[]{"rating_id"});
            stmt.setString(1, rating.getName());
            return stmt;
        }, keyHolder);
        return jdbcTemplate.query("SELECT * FROM rating WHERE rating_id = ?", new RatingMapper(), keyHolder.getKey().longValue())
                .stream().findFirst().orElse(null);
    }

    @Override
    public List<Rating> getAll() {
        return jdbcTemplate.query("SELECT * FROM rating ORDER BY RATING_ID ASC", new RatingMapper());
    }

    @Override
    public Optional<Rating> getById(int id) {
        return jdbcTemplate.query("SELECT * FROM rating WHERE rating_id = ?", new RatingMapper(), id)
                .stream().findFirst();
    }

    @Override
    public Rating update(Rating rating) {
        jdbcTemplate.update("UPDATE rating SET RATING_NAME = ? WHERE RATING_ID = ?", rating.getName(), rating.getId());
        return jdbcTemplate.query("SELECT * FROM rating WHERE rating_id = ?", new RatingMapper(), rating.getId())
                .stream().findFirst().orElse(null);
    }

    @Override
    public void delete(int id) {
        jdbcTemplate.update("DELETE FROM rating WHERE rating_id = ?", id);
    }

    private class RatingMapper implements RowMapper<Rating> {
        @Override
        public Rating mapRow(ResultSet rs, int rowNum) throws SQLException {
            Rating rating = new Rating();

            rating.setId(rs.getInt("rating_id"));
            rating.setName(rs.getString("rating_name"));

            return rating;
        }
    }

}
