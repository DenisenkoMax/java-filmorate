package ru.yandex.practicum.filmorate;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import ru.yandex.practicum.filmorate.contorller.UserController;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.User;

import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

@SpringBootTest
public class UserControllerValidationTests {
    private static final UserController userController = new UserController();

    private User user;

    @BeforeEach
    private void addModels() {
        this.user = User.builder()
                .id(1)
                .email("dsd@yandex.ru")
                .login("Vasya")
                .name("Vasilii")
                .birthday(LocalDate.of(1980, 1, 1))
                .build();
    }

    @Test
    void emptyEmail() {
        this.user.setEmail("");
        Exception exception = assertThrows(ValidationException.class, () -> userController.create(user));
        assertEquals("Электронная почта не может быть пустой и должна содержать символ @", exception.getMessage());
    }

    @Test
    void withoutCommercialAtEmail() {
        this.user.setEmail("wdsdwdfyandex.ru");
        Exception exception = assertThrows(ValidationException.class, () -> userController.create(user));
        assertEquals("Электронная почта не может быть пустой и должна содержать символ @", exception.getMessage());
    }

    @Test
    void emptyLogin() {
        this.user.setLogin("");
        Exception exception = assertThrows(ValidationException.class, () -> userController.create(user));
        assertEquals("Логин не может быть пустым и содержать пробелы", exception.getMessage());
    }

    @Test
    void SpacesInLogin() {
        this.user.setLogin("super user");
        Exception exception = assertThrows(ValidationException.class, () -> userController.create(user));
        assertEquals("Логин не может быть пустым и содержать пробелы", exception.getMessage());
    }

    @Test
    void birthdayInFuture() {
        this.user.setBirthday(LocalDate.of(2100, 1, 15));
        Exception exception = assertThrows(ValidationException.class, () -> userController.create(user));
        assertEquals("Дата рождения не может быть в будущем", exception.getMessage());
    }

    @Test
    void emptyName() {
        this.user.setName("");
        userController.create(user);
        assertEquals(user.getName(), user.getLogin());
    }

    @Test
    void nullUser() {
        this.user = null;
        Exception exception = assertThrows(ValidationException.class, () -> userController.create(user));
        assertEquals("Отправлен пустой запрос user", exception.getMessage());
    }
}
