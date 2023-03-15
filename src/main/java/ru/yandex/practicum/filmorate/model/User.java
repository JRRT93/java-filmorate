package ru.yandex.practicum.filmorate.model;

import lombok.*;
import ru.yandex.practicum.filmorate.annotation.NotContainSpaces;

import javax.validation.constraints.*;
import java.time.LocalDate;
import java.util.Set;

@Getter
@Setter
@AllArgsConstructor
@EqualsAndHashCode
public class User {
    private Long id;
    @NotBlank
    @Email
    private String email;
    @NotBlank
    @NotContainSpaces
    private String login;
    private String name;
    @Past
    private LocalDate birthday;
    private Set<Long> friends;
}