package ru.yandex.practicum.filmorate.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.dao.FriendshipDao;
import ru.yandex.practicum.filmorate.dao.UserDao;

import java.util.*;
import java.util.stream.Collectors;


@Service
@Slf4j
public class UserService {

    UserDao userDao;
    FriendshipDao friendshipDao;

    @Autowired
    public UserService(UserDao userDao, FriendshipDao friendshipDao) {
        this.userDao = userDao;
        this.friendshipDao = friendshipDao;
    }

    public boolean addFriendship(Long user1Id, Long user2Id) {
        userDao.getUserById(user1Id);
        userDao.getUserById(user2Id);
        List<Long> user1Friends = friendshipDao.getFriendsIds(user1Id);
        List<Long> user2Friends = friendshipDao.getFriendsIds(user2Id);
        if (user2Friends.contains(user1Id)) {
            log.warn("Пользователь с id={} уже добавлял в друзья пользователя с id={}", user1Id, user2Id);
            return false;
        } else if (user1Friends.contains(user2Id)) {
            log.info("Теперь пользователи с id={{},{}} обоюдно стали друзьями.", user1Id, user2Id);
            friendshipDao.updateFriendship(user2Id, user1Id, true);
        } else {
            log.info("Пользователь с id={} стал другом пользователя с id={}", user1Id, user2Id);
            friendshipDao.addFriendship(user1Id, user2Id);
        }
        return true;
    }

    public boolean deleteFriendship(Long user1Id, Long user2Id) {
        if (!friendshipDao.getFriendsIds(user1Id).contains(user2Id)) {
            log.warn("У пользователя с id={} нет в друзьях пользователя с id={}", user1Id, user2Id);
            return false;
        }

        if (friendshipDao.deleteFriendship(user1Id, user2Id)) {
            log.info("Пользователь с id={} убрал из друзей пользователя c id={}", user1Id, user2Id);
            return true;
        } else {
            return false;
        }
    }

    public List<User> getFriends(Long id) {
        User user = userDao.getUserById(id);
        Set<User> users = new HashSet<>();
        for (Long curUserId : user.getFriends()) {
            users.add(userDao.getUserById(curUserId));
        }
        return users.stream()
                .sorted(Comparator.comparingLong(User::getId))
                .collect(Collectors.toList());
    }

    public List<User> getCommonFriends(Long userId, Long friendId) {
        User user = userDao.getUserById(userId);
        User friend = userDao.getUserById(friendId);
        Set<User> commonFriends = new HashSet<>();
        for (Long curFriendId : user.getFriends()) {
            if (friend.getFriends().contains(curFriendId)) {
                commonFriends.add(userDao.getUserById(curFriendId));
            }
        }
        return commonFriends.stream()
                .sorted(Comparator.comparingLong(User::getId))
                .collect(Collectors.toList());
    }
}
