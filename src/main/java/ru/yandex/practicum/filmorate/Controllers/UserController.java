package ru.yandex.practicum.filmorate.Controllers;

import lombok.extern.slf4j.Slf4j;
import ru.yandex.practicum.filmorate.Exception.AlreadyExistException;
import ru.yandex.practicum.filmorate.Exception.NotExistException;
import ru.yandex.practicum.filmorate.Validators.UserValidator;
import ru.yandex.practicum.filmorate.model.User;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

@Slf4j
@RestController
@RequestMapping("/users")
public class UserController {
    private final Map<Long, User> users = new HashMap<>();
    private long userId = 0L;

    @GetMapping
    public Collection<User> allUsers() {
        return new ArrayList<>(users.values());
    }

    @PostMapping
    public User create(@RequestBody User user) {
        UserValidator.validate(user);
        if (!users.containsKey(user.getId())) {
            long id = generatedId();
            user.setId(id);
            users.put(user.getId(), user);
            log.info("Добавлен пользователь " + user.getName());
            return user;
        }
        throw new AlreadyExistException("Пользователь с электронной почтой " +
                user.getEmail() + " уже зарегистрирован.");
    }

    @PutMapping
    public User put(@RequestBody User user) {
        UserValidator.validate(user);
        if (!users.containsKey(user.getId())) {
            throw new NotExistException("Такого пользователя нет");
        }
        users.put(user.getId(), user);
        log.info("Обновлены данные пользователя " + user.getEmail());
        return user;
    }

    public long generatedId() {
        userId++;
        return userId;
    }

}
