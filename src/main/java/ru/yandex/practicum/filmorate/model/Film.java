package ru.yandex.practicum.filmorate.model;

import lombok.*;
import ru.yandex.practicum.filmorate.annotation.LengthLimit;
import ru.yandex.practicum.filmorate.annotation.ReleaseDateLimit;

import javax.validation.constraints.*;
import java.time.LocalDate;
import java.util.Set;

@Getter
@Setter
@AllArgsConstructor
@EqualsAndHashCode
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
}