package ru.yandex.practicum.filmorate;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import ru.yandex.practicum.filmorate.contorller.FilmController;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.service.FilmService;
import ru.yandex.practicum.filmorate.storage.InMemoryFilmStorage;

import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

@SpringBootTest
public class FilmControllerValidationTests {

    private static final FilmController filmController = new FilmController(new FilmService(new InMemoryFilmStorage()));
    private Film film;

    @BeforeEach
    private void addModels() {
        this.film = Film.builder()
                .id(1L)
                .name("Терминатор")
                .description("Терминатор описание")
                .duration(105)
                .releaseDate(LocalDate.of(1984, 10, 24))
                .build();
    }

    @Test
    void emptyName() {
        this.film.setName("");
        Exception exception = assertThrows(ValidationException.class, () -> filmController.create(film));
        assertEquals("Название не может быть пустым", exception.getMessage());
    }

    @Test
    void manySymbolsDescription() {
        this.film.setDescription("Фильм открывается сценой мрачного будущего, где происходит война людей и машин." +
                " Затем действие переносится в настоящее (1984 год). Ночью где-то в Лос-Анджелесе водитель мусоровоза" +
                " становится свидетелем странных вспышек с электрическими");
        Exception exception = assertThrows(ValidationException.class, () -> filmController.create(film));
        assertEquals("Максимальная длина описания — 200 символов", exception.getMessage());
    }

    @Test
    void earlyDate() {
        this.film.setReleaseDate(LocalDate.of(1890, 1, 1));
        Exception exception = assertThrows(ValidationException.class, () -> filmController.create(film));
        assertEquals("Дата релиза должна быть не раньше 28 декабря 1895 года", exception.getMessage());
    }

    @Test
    void minusDuration() {
        this.film.setDuration(-1);
        Exception exception = assertThrows(ValidationException.class, () -> filmController.create(film));
        assertEquals("Продолжительность фильма должна быть положительной", exception.getMessage());
    }

    @Test
    void nullFilm() {
        this.film = null;
        Exception exception = assertThrows(ValidationException.class, () -> filmController.create(film));
        assertEquals("Отправлен пустой запрос film", exception.getMessage());
    }
}
