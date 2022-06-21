package ru.yandex.practicum.filmorate.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exception.MissingObject;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.UserStorage;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
public class UserService {
    private final UserStorage userStorage;

    @Autowired
    public UserService(UserStorage userStorage) {
        this.userStorage = userStorage;
    }

    public void create(User user) {
        validateUser(user);
        userStorage.create(user);
    }

    public Collection<User> findAll() {
        return userStorage.findAll();
    }

    public User getUser(Long id) {
        if (!userStorage.getUsers().containsKey(id)) {
            throw new MissingObject("Объект не найден");
        }
        return userStorage.getUsers().get(id);
    }

    public void put(User user) {
        if (!userStorage.getUsers().containsKey(user.getId())) {
            throw new MissingObject("Объект не найден");
        }
        validateUser(user);
        userStorage.put(user);
    }

    public void addFriend(Long userId, Long friendId) {
        if ((!userStorage.getUsers().containsKey(userId)) || (!userStorage.getUsers().containsKey(friendId))) {
            throw new MissingObject("Объект не найден");
        }

        userStorage.getUsers().get(userId).getFriends().add(friendId);
        userStorage.getUsers().get(friendId).getFriends().add(userId);
    }

    public void removeFriend(Long userId, Long friendId) {
        if ((!userStorage.getUsers().containsKey(userId)) || (!userStorage.getUsers().containsKey(friendId))) {
            throw new MissingObject("Объект не найден");
        }
        userStorage.getUsers().get(userId).getFriends().remove(friendId);
        userStorage.getUsers().get(friendId).getFriends().remove(userId);
    }

    public Collection<User> findMutualFriends(Long userId, Long otherId) {
        List<User> mutualFriends = new ArrayList<>();
        for (Long friendId : userStorage.getUsers().get(userId).getFriends()) {
            if (userStorage.getUsers().get(otherId).getFriends().contains(friendId)) {
                mutualFriends.add(userStorage.getUsers().get(friendId));
            }
        }
        return mutualFriends;
    }

    public Collection<User> findFriends(Long userId) {
        return userStorage.getUsers().values().stream().filter(p ->
                userStorage.getUsers().get(userId).getFriends().contains(p.getId())).collect(Collectors.toList());
    }

    private void validateUser(User user) {
        if (user == null) {
            log.debug("Отправлен пустой запрос user");
            throw new ValidationException("Отправлен пустой запрос user");
        }
        if (user.getEmail() == null || user.getEmail().isBlank() || !user.getEmail().contains("@")) {
            log.debug("Электронная почта не может быть пустой и должна содержать символ: {}", user.getEmail());
            throw new ValidationException("Электронная почта не может быть пустой и должна содержать символ @");
        }
        if ((user.getLogin() == null || user.getLogin().isBlank()) || user.getLogin().contains(" ")) {
            log.debug("Логин не может быть пустым и содержать пробелы: {}", user.getLogin());
            throw new ValidationException("Логин не может быть пустым и содержать пробелы");
        }
        if (LocalDate.now().isBefore(user.getBirthday())) {
            log.debug("Дата рождения не может быть в будущем: {}", user.getBirthday().toString());
            throw new ValidationException("Дата рождения не может быть в будущем");
        }
        if (user.getName() == null || user.getName().isBlank()) {
            log.debug("Имя для отображения может быть пустым. Использован логин: {}", user.getLogin());
            user.setName(user.getLogin());
        }
    }
}
