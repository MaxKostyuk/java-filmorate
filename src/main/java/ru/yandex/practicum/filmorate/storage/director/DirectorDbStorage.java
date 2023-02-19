package ru.yandex.practicum.filmorate.storage.director;

import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Primary;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.filmorate.model.Director;

import java.sql.PreparedStatement;
import java.util.List;
import java.util.Objects;

@Repository
@Primary
@RequiredArgsConstructor
public class DirectorDbStorage implements DirectorStorage {
    private static final String SQL_GET_DIRECTOR =
            "SELECT director_id, name " +
                    "FROM director " +
                    "WHERE director_id = ?";

    private static final String SQL_GET_DIRECTORS =
            "SELECT director_id, name " +
                    "FROM director";

    private static final String SQL_INSERT_DIRECTOR =
            "INSERT INTO director (name) " +
                    "VALUES (?)";

    private static final String SQL_UPDATE_DIRECTOR =
            "UPDATE director SET (name) = (?) " +
                    "WHERE director_id = ?";

    private static final String SQL_DELETE_DIRECTOR =
            "DELETE FROM director " +
                    "WHERE director_id = ?";

    private final JdbcTemplate jdbcTemplate;
    private final DirectorMapper directorMapper;

    @Override
    public Director get(int id) {
        return jdbcTemplate.queryForObject(SQL_GET_DIRECTOR, directorMapper, id);
    }

    @Override
    public List<Director> get() {
        return jdbcTemplate.query(SQL_GET_DIRECTORS, directorMapper);
    }

    @Override
    public int create(Director director) {
        KeyHolder keyHolder = new GeneratedKeyHolder();
        String name = director.getName();

        jdbcTemplate.update(connection -> {
            PreparedStatement ps = connection.prepareStatement(SQL_INSERT_DIRECTOR, new String[]{"director_id"});
            ps.setString(1, name);
            return ps;
        }, keyHolder);

        return Objects.requireNonNull(keyHolder.getKey()).intValue();
    }

    @Override
    public int update(Director director) {
        int directorId = director.getId();
        String name = director.getName();

        return jdbcTemplate.update(SQL_UPDATE_DIRECTOR, name, directorId);
    }

    @Override
    public int delete(int id) {
        return jdbcTemplate.update(SQL_DELETE_DIRECTOR, id);
    }
}
