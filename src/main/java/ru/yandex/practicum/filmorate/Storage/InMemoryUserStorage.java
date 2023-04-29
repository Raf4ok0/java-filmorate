package ru.yandex.practicum.filmorate.Storage;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.Exception.AlreadyExistException;
import ru.yandex.practicum.filmorate.Exception.NotFoundException;
import ru.yandex.practicum.filmorate.model.User;

import java.util.*;

@Slf4j
@Component
public class InMemoryUserStorage implements UserStorage {

    private final Map<Long, User> users = new HashMap<>();
    private long userId = 0;

    @Override
    public User getById(long id) {
        if (!users.containsKey(id)) {
            throw new NotFoundException("Такого пользователя нет");
        }
        return users.get(id);
    }

    @Override
    public User addUser(User user) {
        if (users.containsKey(user.getId())) {
            throw new AlreadyExistException("Пользователь с электронной почтой " +
                    user.getEmail() + " уже зарегистрирован.");
        } else {
            long id = generatedId();
            user.setId(id);
            users.put(user.getId(), user);
            log.info("Добавлен пользователь " + user.getName());
        }
        return user;
    }

    @Override
    public User updateUser(User user) {
        if (!users.containsKey(user.getId())) {
            throw new NotFoundException("Пользователь с электронной почтой " +
                    user.getEmail() + " не найден.");
        }
        users.put(user.getId(), user);
        log.info("Успешное изменение пользователя");
        return user;
    }

    @Override
    public User deleteUser(long id) {
        if (users.containsKey(id)) {
            return users.remove(id);
        } else {
            throw new NotFoundException("Пользователь не найден");
        }
    }

    public long generatedId() {
        userId++;
        return userId;
    }

    @Override
    public List<User> getAllUsersList() {
        return new ArrayList<>(users.values());
    }

    @Override
    public void addFriend(long userId, long friendId) {
        User user1 = getById(userId);
        User user2 = getById(friendId);
        user1.getFriends().add(user2.getId());
    }

    @Override
    public void deleteFriend(long userId, long friendId) {
        User user1 = getById(userId);
        User user2 = getById(friendId);
        user1.getFriends().remove(user2.getId());
    }

}
