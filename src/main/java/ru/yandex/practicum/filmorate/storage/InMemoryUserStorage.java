package ru.yandex.practicum.filmorate.storage;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.User;

import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;

@Slf4j
@Component
public class InMemoryUserStorage implements UserStorage {
    private final Map<Long, User> users = new HashMap<>();
    private Long id = 1L;

    @Override
    public Map<Long, User> getUsers() {
        return users;
    }

    @Override
    public void create(User user) {
        if (!users.containsKey(user.getId())) {
            log.debug("Создание Пользователя {}", user.getName());
            user.setId(id);
            user.setFriends(new HashSet<>());
            users.put(id, user);
            id++;
        }
    }

    @Override
    public Collection<User> findAll() {
        return users.values();
    }

    @Override
    public void put(User user) {
        if (!this.users.containsKey(user.getId())) {
            log.debug("Неверный id");
            throw new ValidationException("Отправлен пустой запрос user");
        }
        if (user.getFriends() == null) {
            user.setFriends(new HashSet<>());
        }
        users.put(user.getId(), user);
    }
}
