package ru.practicum.shareit.item.dto;

import lombok.*;

import java.time.LocalDateTime;

@Getter
@Setter
@ToString
@EqualsAndHashCode
@AllArgsConstructor
public class CommentDto {
    private long id;
    private String text;
    private String authorName;
    private LocalDateTime created;

    public CommentDto() {

    }
}
