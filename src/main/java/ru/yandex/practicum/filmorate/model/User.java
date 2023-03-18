package ru.yandex.practicum.filmorate.model;

import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
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
    private Set<Long> incomingFriendshipRequests;
    private Set<Long> outgoingFriendshipRequests;
    private boolean isFriend;
}