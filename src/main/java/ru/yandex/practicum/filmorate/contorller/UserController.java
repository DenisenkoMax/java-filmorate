package ru.yandex.practicum.filmorate.contorller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.User;

import java.time.LocalDate;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/users")
public class UserController {
    private static final Logger log = LoggerFactory.getLogger(UserController.class);
    private final Map<Integer, User> users = new HashMap<>();
    private Integer id = 1;

    @GetMapping
    public Collection<User> findAll() {
        return users.values();
    }

    @PostMapping
    public User create(@RequestBody User user) {
        userValidation(user);
        if (!users.containsKey(user.getId())) {
            log.debug("Создание Пользователя {}", user.getName());
            user.setId(id);
            users.put(id, user);
            id++;
        }
        return user;
    }

    @PutMapping
    public User put(@RequestBody User user) {
        userValidation(user);
        log.debug("Обновление Пользователя {}", user.getName());
        if (!this.users.containsKey(user.getId())) {
            log.debug("Неверный id");
            throw new ValidationException("Отправлен пустой запрос user");
        }
        users.put(user.getId(), user);
        return user;
    }

    private void userValidation(User user) {
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
            log.debug("Имя для отображения может быть пустым — использован логин: {}", user.getLogin());
            user.setName(user.getLogin());
        }
    }
}
