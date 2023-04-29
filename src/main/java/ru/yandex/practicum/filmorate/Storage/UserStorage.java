package ru.yandex.practicum.filmorate.Storage;

import ru.yandex.practicum.filmorate.model.User;

import java.util.List;

public interface UserStorage {

    User addUser(User user);

    User updateUser(User user);

    User deleteUser(long id);

    List<User> getAllUsersList();

    User getById(long id);

    void addFriend(long userId, long friendId);

    void deleteFriend(long userId, long friendId);
}
