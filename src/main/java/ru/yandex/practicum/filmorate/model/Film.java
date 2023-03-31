package ru.yandex.practicum.filmorate.model;

import lombok.*;
import ru.yandex.practicum.filmorate.annotation.LengthLimit;
import ru.yandex.practicum.filmorate.annotation.ReleaseDateLimit;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Positive;
import java.time.LocalDate;
import java.util.Set;
import java.util.TreeSet;

@Getter
@Setter
@AllArgsConstructor
@EqualsAndHashCode
@Builder
public class Film {
    private Long id;
    @NotBlank
    private String name;
    @LengthLimit
    private String description;
    @Positive
    private int duration;
    @ReleaseDateLimit
    private LocalDate releaseDate;
    private Set<Long> likes;
    private Rating mpa;
    private TreeSet<Genre> genres;
}