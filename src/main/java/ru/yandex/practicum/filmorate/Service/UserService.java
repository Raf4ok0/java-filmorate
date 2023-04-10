package ru.yandex.practicum.filmorate.Service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.Exception.NotFoundException;
import ru.yandex.practicum.filmorate.Storage.UserStorage;
import ru.yandex.practicum.filmorate.Validators.UserValidator;
import ru.yandex.practicum.filmorate.model.User;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class UserService {

    private final UserStorage storage;

    public List<User> getAllUsersList() {
        return storage.getAllUsersList();
    }

    public User getById(long id) {
        return storage.getById(id);
    }

    public User deleteUser(long id) {
        return storage.deleteUser(id);
    }

    public User addUser(User user) {
        UserValidator.validate(user);
        return storage.addUser(user);
    }

    public User updateUser(User user) {
        UserValidator.validate(user);
        return storage.updateUser(user);
    }

    public void addFriend(long userId, long friendId) {
        checkExistUser(userId);
        checkExistUser(friendId);
        storage.getById(userId).getFriends().add(friendId);
        storage.getById(friendId).getFriends().add(userId);
        log.info("Новый друг добавлен");
    }

    public void deleteFriend(long userId, long friendId) {
        checkExistUser(userId);
        checkExistUser(friendId);
        storage.getById(userId).getFriends().remove(friendId);
        storage.getById(friendId).getFriends().remove(userId);
        log.info("Друг удален");
    }

    public List<User> getFriendsList(long userId) {
        checkExistUser(userId);
        return storage.getById(userId).getFriends().stream()
                .map(id -> storage.getById(id))
                .collect(Collectors.toList());
    }

    public List<User> getCommonFriendsList(long userId, long otherId) {
        checkExistUser(userId);
        checkExistUser(otherId);
        Set<Long> userList = storage.getById(userId).getFriends();
        Set<Long> otherList = storage.getById(otherId).getFriends();
        return userList.stream()
                .filter(otherList::contains)
                .map(id -> storage.getById(id))
                .collect(Collectors.toList());
    }

    public void checkExistUser(Long userId) {
        if (storage.getById(userId) == null) {
            throw new NotFoundException("Пользователь с таким ID не найден.");
        }
    }
}
