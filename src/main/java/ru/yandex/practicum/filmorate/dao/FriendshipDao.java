package ru.yandex.practicum.filmorate.dao;

import java.util.List;

public interface FriendshipDao {
    List<Long> getFriendsIds(Long id);

    boolean addFriendship(Long user1Id, Long user2Id);

    boolean updateFriendship(Long user1Id, Long user2Id, boolean status);

    boolean deleteFriendship(Long user1Id, Long user2Id);
}
