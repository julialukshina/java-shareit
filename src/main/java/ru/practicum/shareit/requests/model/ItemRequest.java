package ru.practicum.shareit.requests.model;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.time.LocalDate;

@Getter
@Setter
@ToString
@EqualsAndHashCode
public class ItemRequest {
    private long id;
    private String description;
    private long creatorId;
    private LocalDate dateOfCreation;

    public ItemRequest(long id, String description, long creatorId, LocalDate dateOfCreation) {
        this.id = id;
        this.description = description;
        this.creatorId = creatorId;
        this.dateOfCreation = dateOfCreation;
    }
}
