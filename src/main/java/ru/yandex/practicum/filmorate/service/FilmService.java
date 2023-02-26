package ru.yandex.practicum.filmorate.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.storage.FIlmStorage;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class FilmService {
    private final FIlmStorage fIlmStorage;
    private long id = 1;

    public Film addFilm (Film film) {
        film.setId(id);
        id++;
        fIlmStorage.addFilm(film);
        log.debug("FILM successful added. ID=" + film.getId());
        return film;
    }

    public Film updateFilm(Film film) throws ValidationException {
        fIlmStorage.updateFilm(film);
        log.debug("FILM successful updated. ID=" + film.getId());
        return film;
    }

    public List<Film> getAllFilms() {
        return fIlmStorage.getAllFilms();
    }

    public Film findFilmByID(long id) throws ValidationException {
        return fIlmStorage.findFilmByID(id);
    }

    public void addFilmLike(long id, long userID) throws ValidationException {
        fIlmStorage.findFilmByID(id).getLikes().add(userID);
    }

    public void deleteFilmLike(long id, long userID) throws ValidationException {
        fIlmStorage.findFilmByID(id).getLikes().remove(userID);
    }

    public List<Film> findPopularFilms (int count) {
        List<Film> popularFilms = new ArrayList<>();
        Comparator<Film> comparator = (Film film1, Film film2) -> film2.getLikes().size() - (film1.getLikes().size());
        List<Film> sortedList = fIlmStorage.getAllFilms();
        sortedList.sort(comparator);
        if (count == 0) {
            if (sortedList.size() <= 10) return sortedList;
            for (int i = 0; i < 10; i++) {
                popularFilms.add(sortedList.get(i));
            }
        } else {
            for (int i = 0; i < count; i++) {
                popularFilms.add(sortedList.get(i));
            }
        }
        return popularFilms;
    }
}
