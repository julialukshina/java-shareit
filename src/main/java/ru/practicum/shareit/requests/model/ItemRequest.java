package ru.practicum.shareit.requests.model;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import ru.practicum.shareit.user.model.User;

import java.time.LocalDate;

@Getter
@Setter
@ToString
@EqualsAndHashCode
public class ItemRequest {
    private long id;
    private String description;
    private User creator;
    private LocalDate dateOfCreation;

    public ItemRequest(long id, String description, User creator, LocalDate dateOfCreation) {
        this.id = id;
        this.description = description;
        this.creator = creator;
        this.dateOfCreation = dateOfCreation;
    }
}
