package ru.yandex.practicum.filmorate.storage.event;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.model.UserEvent;

import java.sql.PreparedStatement;
import java.sql.Timestamp;
import java.time.LocalDateTime;

@Component
public class EventStorage {
    private final JdbcTemplate jdbcTemplate;

    @Autowired
    public EventStorage(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    public void createEvent(UserEvent event) {
        String sqlQuery = "insert into USER_EVENTS(user_id, event_type, TIMESTAMP, ENTITY_ID, OPERATION) values (?,?,?,?,?)";
        jdbcTemplate.update(connection -> {
            PreparedStatement stmt = connection.prepareStatement(sqlQuery);
            stmt.setInt(1, event.getUserId());
            stmt.setString(2, event.getEventType().toString());
            stmt.setTimestamp(3, Timestamp.valueOf(LocalDateTime.now()));
            stmt.setInt(4, event.getEntityId());
            stmt.setString(5, event.getOperation().toString());
            return stmt;
        });
    }

    public void getEvent(int id) {
        String sql = "SELECT event_id, user_id, event_type, event_date FROM USER_EVENT WHERE event_id = ?";
    }
}
