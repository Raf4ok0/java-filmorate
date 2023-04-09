package ru.yandex.practicum.filmorate.Service;

import lombok.extern.slf4j.Slf4j;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.Exception.NotFoundException;
import ru.yandex.practicum.filmorate.Storage.UserStorage;
import ru.yandex.practicum.filmorate.model.User;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Slf4j
@Service
public class UserService {

    public final UserStorage inMemoryUserStorage;

    @Autowired
    public UserService(UserStorage inMemoryUserStorage) {
        this.inMemoryUserStorage = inMemoryUserStorage;
    }

    public void addFriend(long userId, long friendId) {
        checkExistUser(userId);
        checkExistUser(friendId);
        inMemoryUserStorage.getById(userId).getFriends().add(friendId);
        inMemoryUserStorage.getById(friendId).getFriends().add(userId);
        log.info("Новый друг добавлен");
    }

    public void deleteFriend(long userId, long friendId) {
        checkExistUser(userId);
        checkExistUser(friendId);
        inMemoryUserStorage.getById(userId).getFriends().remove(friendId);
        inMemoryUserStorage.getById(friendId).getFriends().remove(userId);
        log.info("Друг удален");
    }

    public List<User> getFriendsList(long userId) {
        checkExistUser(userId);
        List<User> userFriends = new ArrayList<>();
        for (Long id : inMemoryUserStorage.getById(userId).getFriends()) {
            userFriends.add(inMemoryUserStorage.getById(id));
        }
        return userFriends;
    }

    public List<User> getCommonFriendsList(long userId, long otherId) {
        checkExistUser(userId);
        checkExistUser(otherId);
        List<User> commonFriends = new ArrayList<>();
        Set<Long> userList = inMemoryUserStorage.getById(userId).getFriends();
        Set<Long> otherList = inMemoryUserStorage.getById(otherId).getFriends();
        Set<Long> commonListIds = userList.stream()
                .distinct()
                .filter(otherList::contains)
                .collect(Collectors.toSet());
        for (Long id : commonListIds) {
            commonFriends.add(inMemoryUserStorage.getById(id));
        }
        return commonFriends;
    }

    public void checkExistUser(Long userId) {
        if (inMemoryUserStorage.getById(userId) == null) {
            throw new NotFoundException("Пользователь с таким ID не найден.");
        }
    }
}
