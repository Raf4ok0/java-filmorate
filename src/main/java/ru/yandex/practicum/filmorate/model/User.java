package ru.yandex.practicum.filmorate.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;
import java.util.HashSet;
import java.util.Set;

@Getter
@Setter
@AllArgsConstructor
public class User {
    private long id;
    private String email;
    private String login;
    private String name;
    private LocalDate birthday;
    public final Set<Long> friends = new HashSet<>();
}
