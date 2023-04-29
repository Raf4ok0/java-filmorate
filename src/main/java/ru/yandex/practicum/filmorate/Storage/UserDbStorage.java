package ru.yandex.practicum.filmorate.Storage;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Primary;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.BatchPreparedStatementSetter;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.jdbc.support.rowset.SqlRowSet;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.filmorate.Exception.NotFoundException;
import ru.yandex.practicum.filmorate.model.User;

import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Repository
@Slf4j
@Qualifier
@Primary
@Component("UserDbStorage")
public class UserDbStorage implements UserStorage {
    public UserDbStorage(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    private final JdbcTemplate jdbcTemplate;

    @Override
    public User addUser(User user) {
        final String sqlQuery = "INSERT INTO users (EMAIL, LOGIN, NAME, BIRTHDAY) " +
                "VALUES ( ?, ?, ?, ?)";
        KeyHolder keyHolder = new GeneratedKeyHolder();
        jdbcTemplate.update(connection -> {
            PreparedStatement statement = connection.prepareStatement(sqlQuery, new String[]{"user_id"});
            statement.setString(1, user.getEmail());
            statement.setString(2, user.getLogin());
            statement.setString(3, user.getName());
            statement.setDate(4, Date.valueOf(user.getBirthday()));
            return statement;
        }, keyHolder);

        log.info("Пользователь создан" + user.getLogin());
        user.setId(keyHolder.getKey().intValue());
        insertFriends(user.getFriends(), user.getId());
        return user;
    }

    @Override
    public List<User> getAllUsersList() {
        final String sqlQuery = "SELECT * FROM users";
        log.info("Список пользователей отправлен");
        return jdbcTemplate.query(sqlQuery, this::makeUser);
    }

    private void insertFriends(Set<Long> friendIds, long userId) {
        if (friendIds.size() == 0) {
            return;
        }
        List<Long> list = List.copyOf(friendIds);
        jdbcTemplate.update("DELETE FROM friendship WHERE FRIEND_ID=?", userId);
        jdbcTemplate.batchUpdate("INSERT INTO friendship (FRIEND_ID,OTHER_FRIEND_ID) VALUES (?,?)",
                new BatchPreparedStatementSetter() {
                    @Override
                    public void setValues(PreparedStatement ps, int i) throws SQLException {
                        ps.setLong(1, userId);
                        ps.setLong(2, list.get(i));
                    }

                    @Override
                    public int getBatchSize() {
                        return friendIds.size();
                    }
                }
        );
    }

    @Override
    public User updateUser(User user) {
        final String updateQuery = "UPDATE users SET EMAIL = ?, LOGIN = ?, NAME = ?, BIRTHDAY = ?" + "WHERE user_id = ?";
        int cnt = jdbcTemplate.update(updateQuery, user.getEmail(), user.getLogin(), user.getName(), user.getBirthday(), user.getId());
        if (cnt == 0) {
            log.info("Пользователь не найден", user.getId());
            throw new NotFoundException("Пользователь не найден.");
        }
        insertFriends(user.getFriends(), user.getId());
        log.info("Пользователь обновлен", user.getId());
        return user;
    }

    @Override
    public User getById(long id) {
        try {
            final String getUser = "select * from users where user_id = ?";
            User user = jdbcTemplate.queryForObject(getUser, this::makeUser, id);
            log.info("Пользователь отправлен", id);
            return user;
        } catch (DataAccessException e) {
            log.info("Пользователь не найден", id);
            throw new NotFoundException("Пользователь не найден.");
        }
    }

    @Override
    public User deleteUser(long id) {
        final String deleteUser = "DELETE FROM users WHERE user_id = ?";
        User user = getById(id);
        jdbcTemplate.update(deleteUser, id);
        log.info("Пользователь удален", id);
        return user;
    }

    @Override
    public void addFriend(long firstId, long secondId) {
        checkIfExist(firstId, secondId);

        SqlRowSet rs = jdbcTemplate.queryForRowSet(
                "SELECT * FROM friendship WHERE friend_id=? AND other_friend_id=?",
                firstId, secondId
        );
        if (!rs.next()) {
            final String sqlForWriteQuery = "INSERT INTO friendship (friend_id, other_friend_id) " +
                    "VALUES (?, ?)";
            jdbcTemplate.update(sqlForWriteQuery, firstId, secondId);
        }
    }

    @Override
    public void deleteFriend(long firstId, long secondId) {
        final String sqlQuery = "DELETE FROM friendship WHERE friend_id = ? and other_friend_id = ?";
        jdbcTemplate.update(sqlQuery, firstId, secondId);
    }

    private Set<Long> findFriends(long user_id) {
        return new HashSet(jdbcTemplate.queryForList("SELECT OTHER_FRIEND_ID FROM friendship WHERE FRIEND_ID=?",
                new Object[]{user_id}, Long.class));
    }

    private User makeUser(ResultSet rs, int rowNum) throws SQLException {
        long id = rs.getLong("user_id");
        String email = rs.getString("email");
        String login = rs.getString("login");
        String name = rs.getString("name");
        LocalDate birthday = rs.getDate("birthday").toLocalDate();
        return new User(id, email, login, name, birthday, findFriends(id));
    }

    private void checkIfExist(long firstId, long secondId) {
        final String check = "SELECT 1 where exists(select * FROM users WHERE user_id=?) " +
                "and exists(select * FROM users WHERE user_id=?)";
        SqlRowSet followingRow = jdbcTemplate.queryForRowSet(check, firstId, secondId);
        if (!followingRow.next()) {
            log.warn("Пользователи не найдены", firstId, secondId);
            throw new NotFoundException("Пользователи не найдены");
        }
    }
}
