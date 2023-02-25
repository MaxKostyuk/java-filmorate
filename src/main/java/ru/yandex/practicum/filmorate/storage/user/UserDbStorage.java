package ru.yandex.practicum.filmorate.storage.user;

import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Primary;
import org.springframework.jdbc.core.BatchPreparedStatementSetter;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.model.UserEvent;

import java.sql.Date;
import java.sql.*;
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

@Repository
@Primary
@RequiredArgsConstructor
public class UserDbStorage implements UserStorage {

    private final JdbcTemplate jdbcTemplate;

    @Override
    public User create(User user) {
        String sqlQuery = "insert into USER_LIST(email, login, name, birthday) values (?,?,?,?)";
        KeyHolder keyHolder = new GeneratedKeyHolder();
        jdbcTemplate.update(connection -> {
            PreparedStatement stmt = connection.prepareStatement(sqlQuery, new String[]{"user_id"});
            stmt.setString(1, user.getEmail());
            stmt.setString(2, user.getLogin());
            stmt.setString(3, user.getName());
            stmt.setDate(4, Date.valueOf(user.getBirthday()));
            return stmt;
        }, keyHolder);
        user.setId(keyHolder.getKey().intValue());
        return user;
    }

    @Override
    public List<User> getAll() {
        List<User> users = jdbcTemplate.query("SELECT * FROM user_list ORDER BY USER_ID", new UserMapper());
        for (User user : users) {
            user.setFriendsList(getFriends(user));
        }
        return users;
    }

    @Override
    public Optional<User> getById(int id) {
        String sql = "SELECT * FROM USER_LIST WHERE user_id = ?";
        Optional<User> user = jdbcTemplate.query(sql, new UserMapper(), id).stream().findFirst();
        if (user.isPresent()) {
            user.get().setFriendsList(getFriends(user.get()));
        }
        return user;
    }

    @Override
    public User update(User user) {
        String sql = "UPDATE user_list SET email = ?, login = ?, name = ?, birthday = ? WHERE USER_ID = ?";
        jdbcTemplate.update(sql, user.getEmail(), user.getLogin(), user.getName(), user.getBirthday(), user.getId());
        updateFriends(user);
        return getById(user.getId()).get();
    }

    @Override
    public void delete(int id) {
        jdbcTemplate.update("DELETE FROM USER_LIST WHERE USER_ID = ?", id);
    }

    @Override
    public List<UserEvent> getUserEvents(int userId) {
        String sql = "select * FROM USER_EVENTS as u " +
        "WHERE u.USER_ID = ?  " +
                "ORDER BY EVENT_ID;";
        return jdbcTemplate.query(sql, new Object[]{userId}, (rs, rowNum) ->
                new UserEvent(
                        rs.getInt("event_id"),
                        rs.getString("event_type"),
                        rs.getString("operation"),
                        rs.getInt("user_id"),
                        rs.getInt("entity_id"),
                        rs.getTimestamp("timestamp").getTime()
                )
        );
    }

    private Set<Integer> getFriends(User user) {
        String sqlFriends = "SELECT accepter_id FROM friends_list WHERE sender_id = ?";
        return jdbcTemplate.query(sqlFriends, new FriendsMapper(), user.getId())
                .stream().collect(Collectors.toSet());
    }

    private void updateFriends(User user) {
        Set<Integer> newFriendsList = user.getFriendsList();
        Set<Integer> oldFriendsList = getFriends(user);
        Set<Integer> friendsToAdd = new HashSet<>(newFriendsList);
        friendsToAdd.removeAll(oldFriendsList);
        Set<Integer> friendsToDelete = new HashSet<>(oldFriendsList);
        friendsToDelete.removeAll(newFriendsList);
        ArrayList<Integer> friendsToDeleteAsList = new ArrayList<>(friendsToDelete);
        if (!friendsToDeleteAsList.isEmpty()) {
            jdbcTemplate.batchUpdate("DELETE FROM FRIENDS_LIST WHERE SENDER_ID = ? AND ACCEPTER_ID = ?", new BatchPreparedStatementSetter() {
                @Override
                public void setValues(PreparedStatement ps, int i) throws SQLException {
                    ps.setInt(1, user.getId());
                    ps.setInt(2, friendsToDeleteAsList.get(i));
                }

                @Override
                public int getBatchSize() {
                    return friendsToDeleteAsList.size();
                }
            });
        }
        ArrayList<Integer> friendsToAddAsList = new ArrayList<>(friendsToAdd);
        if (!friendsToAddAsList.isEmpty()) {
            jdbcTemplate.batchUpdate("INSERT INTO FRIENDS_LIST (SENDER_ID, ACCEPTER_ID, STATUS_ID) VALUES (?,?,?)", new BatchPreparedStatementSetter() {
                @Override
                public void setValues(PreparedStatement ps, int i) throws SQLException {
                    ps.setInt(1, user.getId());
                    ps.setInt(2, friendsToAddAsList.get(i));
                    ps.setInt(3, 1);
                }

                @Override
                public int getBatchSize() {
                    return friendsToAddAsList.size();
                }
            });
        }
    }

    private class UserMapper implements RowMapper<User> {


        @Override
        public User mapRow(ResultSet rs, int rowNum) throws SQLException {
            User user = new User();

            user.setId(rs.getInt("user_id"));
            user.setEmail(rs.getString("email"));
            user.setLogin(rs.getString("login"));
            user.setName(rs.getString("name"));
            user.setBirthday(rs.getDate("birthday").toLocalDate());

            return user;
        }
    }

    private class FriendsMapper implements RowMapper<Integer> {

        @Override
        public Integer mapRow(ResultSet rs, int rowNum) throws SQLException {
            return rs.getInt("accepter_id");
        }
    }

}
