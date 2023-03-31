package ru.yandex.practicum.filmorate.storage;

import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.dao.FilmDao;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.Film;

import java.util.List;

@Component
@Primary
@RequiredArgsConstructor
public class FilmDbStorage implements FIlmStorage {
    private final FilmDao filmDao;

    @Override
    public Long addFilm(Film film) {
        return filmDao.createFilm(film);
    }

    @Override
    public boolean updateFilm(Film film) throws ValidationException {
        return filmDao.updateFilm(film);
    }

    @Override
    public Film findFilmById(long id) throws ValidationException {
        return filmDao.findFilmById(id);
    }

    @Override
    public List<Film> getAllFilms() {
        return filmDao.findAllFilms();
    }
}
