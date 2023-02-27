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
import java.util.TreeSet;

@Slf4j
@Service
@RequiredArgsConstructor
public class FilmService {
    private final FIlmStorage fIlmStorage;
    private long id = 1;

    public Film addFilm (Film film) {
        film.setId(id);
        id++;
        if(film.getLikes() == null) film.setLikes(new TreeSet<>());
        log.debug("LIKES field initialized");
        fIlmStorage.addFilm(film);
        log.debug("FILM successful added. ID=" + film.getId());
        return film;
    }

    public Film updateFilm(Film film) throws ValidationException {
        if(film.getLikes() == null) film.setLikes(new TreeSet<>());
        log.debug("LIKES field initialized");
        fIlmStorage.updateFilm(film);
        log.debug("FILM successful updated. ID=" + film.getId());
        return film;
    }

    public List<Film> getAllFilms() {
        return fIlmStorage.getAllFilms();
    }

    public Film findFilmById(long id) throws ValidationException {
        return fIlmStorage.findFilmById(id);
    }

    public void addFilmLike(long id, long userId) throws ValidationException {
        fIlmStorage.findFilmById(id).getLikes().add(userId);
        log.debug("LIKE for film ID=" + id + " from User ID=" + userId + " added");
    }

    public void deleteFilmLike(long id, long userId) throws ValidationException {
        fIlmStorage.findFilmById(id).getLikes().remove(userId);
        log.debug("LIKE for film ID=" + id + " from User ID=" + userId + " deleted");
    }

    public List<Film> findPopularFilms (int count) {
        List<Film> popularFilms = new ArrayList<>();
        Comparator<Film> comparator = (Film film1, Film film2) -> film2.getLikes().size() - (film1.getLikes().size());
        List<Film> sortedList = fIlmStorage.getAllFilms();
        sortedList.sort(comparator);
        if (count == 0) {
            if (sortedList.size() <= 10) {
                log.debug("LIST OF POPULAR FILMS provided. No COUNT requested, list SIZE=" + sortedList.size());
                return sortedList;
            }
            for (int i = 0; i < 10; i++) {
                popularFilms.add(sortedList.get(i));
            }
            log.debug("LIST OF POPULAR FILMS provided. No COUNT requested, list SIZE=" + popularFilms.size());
        } else {
            for (int i = 0; i < count; i++) {
                popularFilms.add(sortedList.get(i));
            }
            log.debug("LIST OF POPULAR FILMS provided. Requested COUNT=" + count + ", list SIZE=" + popularFilms.size());
        }
        return popularFilms;
    }
}