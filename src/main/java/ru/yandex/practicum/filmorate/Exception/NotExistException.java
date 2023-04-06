package ru.yandex.practicum.filmorate.Exception;

public class NotExistException extends RuntimeException {
    public NotExistException(String s) {
        super(s);
    }
}
