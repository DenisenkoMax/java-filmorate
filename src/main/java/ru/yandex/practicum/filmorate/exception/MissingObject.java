package ru.yandex.practicum.filmorate.exception;

public class MissingObject extends RuntimeException {
    public MissingObject(String s) {
        super(s);
    }
}
