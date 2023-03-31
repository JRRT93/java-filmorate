package ru.yandex.practicum.filmorate.model;

import lombok.*;
import ru.yandex.practicum.filmorate.annotation.NotContainSpaces;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Past;
import java.time.LocalDate;
import java.util.Set;

@Getter
@Setter
@AllArgsConstructor
@EqualsAndHashCode
@Builder
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