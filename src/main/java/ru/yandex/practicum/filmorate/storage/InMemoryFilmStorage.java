package ru.yandex.practicum.filmorate.storage;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.Film;

import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;

@Slf4j
@Component
public class InMemoryFilmStorage implements FilmStorage {
    private final Map<Long, Film> films = new HashMap<>();
    private Long id = 1L;

    @Override
    public Map<Long, Film> getFilms() {
        return films;
    }

    @Override
    public void create(Film film) {
        if (!films.containsKey(film.getId())) {
            log.debug("Создание Фильма {}", film.getName());
            film.setId(id);
            film.setLikes(new HashSet<>());
            films.put(id, film);
            id++;
        }
    }

    @Override
    public Collection<Film> findAll() {
        return films.values();
    }

    @Override
    public void put(Film film) {
        log.debug("Обновление фильма {}", film.getName());
        if (!this.films.containsKey(film.getId())) {
            log.debug("Неверный id");
            throw new ValidationException("Отправлен пустой запрос user");
        }
        if (film.getLikes() == null) {
            film.setLikes(new HashSet<>());
        }
        films.put(film.getId(), film);
    }
}
