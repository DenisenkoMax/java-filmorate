package ru.yandex.practicum.filmorate.storage;

import ru.yandex.practicum.filmorate.model.User;

import java.util.Collection;
import java.util.Map;

public interface UserStorage {
    Map<Long, User> getUsers();

    void create(User user);

    Collection<User> findAll();

    void put(User user);
}
