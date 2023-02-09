package ru.yandex.practicum.filmorate.storage.user;

import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Primary;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.filmorate.model.User;

import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
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
        for(User user : users) {
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
        for (int i : friendsToDelete) {
            jdbcTemplate.update("DELETE FROM FRIENDS_LIST WHERE SENDER_ID = ? AND ACCEPTER_ID = ?", user.getId(), i);
        }
        for (int i : friendsToAdd) {
            jdbcTemplate.update("INSERT INTO FRIENDS_LIST (SENDER_ID, ACCEPTER_ID, STATUS_ID) VALUES (?,?,?)", user.getId(), i, 1);
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
