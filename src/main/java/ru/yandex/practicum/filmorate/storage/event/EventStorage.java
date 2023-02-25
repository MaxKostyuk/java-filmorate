package ru.yandex.practicum.filmorate.storage.event;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.model.UserEvent;

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
        jdbcTemplate.update(sqlQuery,
                event.getUserId(),
                event.getEventType(),
                Timestamp.valueOf(LocalDateTime.now()),
                event.getEntityId(),
                event.getOperation());
    }

    public void getEvent(int id) {
        String sql = "SELECT event_id, user_id, event_type, event_date FROM USER_EVENT WHERE event_id = ?";
    }
}
