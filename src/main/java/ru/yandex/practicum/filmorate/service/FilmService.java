package ru.yandex.practicum.filmorate.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.model.dao.GenreDao;
import ru.yandex.practicum.filmorate.model.dao.RatingDao;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.model.Rating;
import ru.yandex.practicum.filmorate.storage.FIlmStorage;

import java.util.*;

@Slf4j
@Service
@RequiredArgsConstructor
public class FilmService {
    private final FIlmStorage fIlmStorage;
    private final RatingDao ratingDao;
    private final GenreDao genreDao;

    public Film addFilm(Film film) {
        if (film.getLikes() == null) film.setLikes(new TreeSet<>());
        log.debug("LIKES field initialized");
        if (film.getGenres() == null) film.setGenres(new TreeSet<>());
        log.debug("GENRES field initialized");
        film.setId(fIlmStorage.addFilm(film));
        log.debug("FILM successful added. ID=" + film.getId());
        return film;
    }

    public Film updateFilm(Film film) throws ValidationException {
        fIlmStorage.findFilmById(film.getId());
        if (film.getLikes() == null) film.setLikes(new TreeSet<>());
        log.debug("LIKES field initialized");
        if (film.getGenres() == null) film.setGenres(new TreeSet<>());
        log.debug("GENRES field initialized");
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
        Film film = fIlmStorage.findFilmById(id);
        film.getLikes().add(userId);
        if (fIlmStorage.updateFilm(film)) {
            log.debug("LIKE for film ID=" + id + " from User ID=" + userId + " added");
        }
    }

    public void deleteFilmLike(long id, long userId) throws ValidationException {
        Film film = fIlmStorage.findFilmById(id);
        film.getLikes().remove(userId);
        if (fIlmStorage.updateFilm(film)) {
            log.debug("LIKE for film ID=" + id + " from User ID=" + userId + " deleted");
        }
    }

    public List<Film> findPopularFilms(int count) {
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

    public List<Genre> getAllGenres() {
        return genreDao.getAllGenres();
    }

    public Genre findGenreById(long id) throws ValidationException {
        return genreDao.findGenreById(id);
    }

    public List<Rating> getAllRatings() {
        return ratingDao.getAllRatings();
    }

    public Rating findRatingById(long id) throws ValidationException {
        return ratingDao.findRatingById(id);
    }
}