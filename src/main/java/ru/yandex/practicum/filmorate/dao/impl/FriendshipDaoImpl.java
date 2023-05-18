package ru.yandex.practicum.filmorate.dao.impl;

import lombok.AccessLevel;
import lombok.experimental.FieldDefaults;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.dao.FriendshipDao;

import java.util.List;
import java.util.Objects;

@Component
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class FriendshipDaoImpl implements FriendshipDao {
    JdbcTemplate jdbcTemplate;

    @Autowired
    public FriendshipDaoImpl(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    @Override
    public List<Long> getFriendsIds(Long id) {
        String sql = "select friend_id from friendships where user_id = ? and status = true " +
                "union select user_id as friend_id from friendships where friend_id = ?";
        return jdbcTemplate.query(sql, ((rs, rowNum) -> rs.getLong("friend_id")), id, id);
    }

    @Override
    public boolean addFriendship(Long user1Id, Long user2Id) {
        if (Objects.equals(user1Id, user2Id)) {
            return false;
        }

        String sql = "insert into friendships (user_id, friend_id, status) " +
                "values (?, ?, false)";
        return jdbcTemplate.update(sql, user1Id, user2Id) > 0;
    }

    @Override
    public boolean updateFriendship(Long user1Id, Long user2Id, boolean status) {
        if (Objects.equals(user1Id, user2Id)) {
            return false;
        }

        String sql = "update friendships " +
                "set status = ? " +
                "where user_id = ? and friend_id = ?";

        return jdbcTemplate.update(sql, status, user1Id, user2Id) > 0;
    }

    @Override
    public boolean deleteFriendship(Long user1Id, Long user2Id) {
        String sql = "delete from friendships " +
                "where (user_id = ? and friend_id = ?) or " +
                "(friend_id = ? and user_id = ?)";

        return jdbcTemplate.update(sql, user1Id, user2Id, user1Id, user2Id) > 0;
    }
}
