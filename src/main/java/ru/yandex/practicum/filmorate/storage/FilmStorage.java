package ru.yandex.practicum.filmorate.storage;

import ru.yandex.practicum.filmorate.model.Film;

import java.util.Collection;
import java.util.Map;

public interface FilmStorage {
    Map<Long, Film> getFilms();

    void create(Film film);

    Collection<Film> findAll();

    void put(Film film);
}

