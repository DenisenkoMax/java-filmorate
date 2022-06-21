package ru.yandex.practicum.filmorate.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exception.MissingObject;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.storage.FilmStorage;

import java.time.LocalDate;
import java.util.Collection;
import java.util.Comparator;
import java.util.stream.Collectors;

@Slf4j
@Service
public class FilmService {
    private static final LocalDate VALIDATION_DATE = LocalDate.of(1895, 12, 28);
    private final FilmStorage filmStorage;


    @Autowired
    public FilmService(FilmStorage filmStorage) {
        this.filmStorage = filmStorage;
    }

    public void create(Film film) {
        validateFilm(film);
        filmStorage.create(film);
    }

    public Film getFilm(Long id) {
        if (!filmStorage.getFilms().containsKey(id)) {
            throw new MissingObject("Объект не найден");
        }
        return filmStorage.getFilms().get(id);
    }

    public void put(Film film) {
        if (!filmStorage.getFilms().containsKey(film.getId())) {
            throw new MissingObject("Объект не найден");
        }
        validateFilm(film);
        filmStorage.put(film);
    }


    public Collection<Film> findAll() {
        return filmStorage.findAll();
    }

    public void addLike(Long filmId, Long userId) {
        if (!filmStorage.getFilms().containsKey(filmId)) {
            throw new MissingObject("Объект не найден");
        }
        filmStorage.getFilms().get(filmId).getLikes().add(userId);
    }

    public void removeLike(Long filmId, Long userId) {
        if (!filmStorage.getFilms().containsKey(filmId) || userId <= 0) {
            throw new MissingObject("Объект не найден");
        }
        filmStorage.getFilms().get(filmId).getLikes().remove(userId);
    }

    public Collection<Film> findTopFilms(Integer size) {
        return filmStorage.getFilms().values().stream().sorted(new Comparator<Film>() {
            @Override
            public int compare(Film o1, Film o2) {
                if (o1.getLikes().size() == o2.getLikes().size())
                    return 0;
                else if (o1.getLikes().size() < o2.getLikes().size()) return 1;
                else return -1;
            }
        }).limit(size).collect(Collectors.toList());
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