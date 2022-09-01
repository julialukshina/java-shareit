package ru.practicum.shareit.requests.model;

import lombok.*;
import ru.practicum.shareit.user.model.User;

import javax.persistence.*;
import javax.validation.constraints.NotBlank;
import java.time.LocalDateTime;

@Entity
@Table(name = "requests")
@Getter
@Setter
@ToString
@EqualsAndHashCode
@AllArgsConstructor
public class ItemRequest {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;
    @NotBlank
    @Column(name = "description", nullable = false)
    private String description;
    @ManyToOne
    @JoinColumn(name = "requester_id")
    private User requester;
    @Column(name = "created", nullable = false)
    private LocalDateTime created;

    public ItemRequest() {

    }
}
