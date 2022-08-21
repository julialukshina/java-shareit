package ru.practicum.shareit.requests.model;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import ru.practicum.shareit.user.model.User;

import javax.persistence.*;
import java.time.LocalDate;

@Entity
@Table(name = "requests")
@Getter
@Setter
@ToString
@EqualsAndHashCode
public class ItemRequest {
    @Id
    private long id;
    private String description;
    @ManyToOne
    @JoinColumn(name = "requestor_id")
    private User creator;
    private LocalDate dateOfCreation;

    public ItemRequest(long id, String description, User creator, LocalDate dateOfCreation) {
        this.id = id;
        this.description = description;
        this.creator = creator;
        this.dateOfCreation = dateOfCreation;
    }

    public ItemRequest() {

    }
}
