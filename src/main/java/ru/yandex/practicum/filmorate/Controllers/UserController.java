package ru.yandex.practicum.filmorate.Controllers;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import ru.yandex.practicum.filmorate.Service.UserService;
import ru.yandex.practicum.filmorate.model.User;
import org.springframework.web.bind.annotation.*;

import java.util.*;

@Slf4j
@RestController
@RequestMapping("/users")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;

    @GetMapping
    public List<User> allUsers() {
        log.info("getAllUsersList");
        return userService.getAllUsersList();
    }

    @PostMapping
    public User create(@RequestBody User user) {
        log.info("addUser");
        return userService.addUser(user);
    }

    @PutMapping
    public User put(@RequestBody User user) {
        log.info("updateUser");
        return userService.updateUser(user);
    }

    @DeleteMapping("/{userId}")
    public User delete(@PathVariable long userId) {
        log.info("");
        return userService.deleteUser(userId);
    }

    @GetMapping("/{id}")
    public User getUser(@PathVariable long id) {
        log.info("getById");
        return userService.getById(id);
    }

    @GetMapping("/{id}/friends")
    public List<User> getFriends(@PathVariable long id) {
        log.info("getFriendsList");
        return userService.getFriendsList(id);
    }

    @GetMapping("/{id}/friends/common/{otherId}")
    public List<User> getCommonFriends(@PathVariable long id, @PathVariable long otherId) {
        log.info("getCommonFriendsList");
        return userService.getCommonFriendsList(id, otherId);
    }

    @PutMapping("/{id}/friends/{friendId}")
    public List<User> addFriend(@PathVariable long id, @PathVariable long friendId) {
        log.info("getFriendsList");
        userService.addFriend(id, friendId);
        return userService.getFriendsList(id);
    }

    @DeleteMapping("/{id}/friends/{friendId}")
    public List<User> deleteFriend(@PathVariable long id, @PathVariable long friendId) {
        log.info("deleteFriend");
        userService.deleteFriend(id, friendId);
        return userService.getFriendsList(id);
    }
}
