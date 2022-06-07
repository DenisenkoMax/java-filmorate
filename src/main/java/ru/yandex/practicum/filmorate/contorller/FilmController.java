package ru.yandex.practicum.filmorate.contorller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.Film;

import java.time.LocalDate;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

@Slf4j
@RestController
@RequestMapping("/films")
public class FilmController {
    private static final LocalDate VALIDATION_DATE = LocalDate.of(1895, 12, 28);
    private final Map<Integer, Film> films = new HashMap<>();
    private Integer id = 1;

    @GetMapping
    public Collection<Film> findAll() {
        return films.values();
    }

    @PostMapping
    public Film create(@RequestBody Film film) {
        validateFilm(film);
        if (!films.containsKey(film.getId())) {
            log.debug("Создание Фильма {}", film.getName());
            film.setId(id);
            films.put(id, film);
            id++;
        }
        return film;
    }

    @PutMapping
    public Film put(@RequestBody Film film) {
        validateFilm(film);
        log.debug("Обновление фильма {}", film.getName());
        if (!this.films.containsKey(film.getId())) {
            log.debug("Неверный id");
            throw new ValidationException("Отправлен пустой запрос user");
        }
        films.put(film.getId(), film);
        return film;
    }

    private void validateFilm(Film film) {
        if (film == null) {
            log.debug("Отправлен пустой запрос film");
            throw new ValidationException("Отправлен пустой запрос film");
        }
        if (film.getName() == null || (film.getName().isBlank())) {
            log.debug("Название не может быть пустым");
            throw new ValidationException("Название не может быть пустым");
        }
        if (film.getDescription().length() > 200) {
            log.debug("Максимальная длина описания — 200 символов: {}", film.getDescription());
            throw new ValidationException("Максимальная длина описания — 200 символов");
        }
        if (film.getReleaseDate().isBefore(VALIDATION_DATE)) {
            log.debug("Дата релиза должна быть — не раньше 28 декабря 1895 года: {}", film.getReleaseDate().toString());
            throw new ValidationException("Дата релиза должна быть не раньше 28 декабря 1895 года");
        }
        if (film.getDuration() <= 0) {
            log.debug("Продолжительность фильма должна быть положительной: {}", film.getDuration());
            throw new ValidationException("Продолжительность фильма должна быть положительной");
        }
    }
}
