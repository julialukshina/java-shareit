package ru.practicum.shareit.items.dto;

import lombok.*;

import javax.validation.constraints.NotBlank;
import java.time.LocalDateTime;

@Getter
@Setter
@ToString
@EqualsAndHashCode
@AllArgsConstructor
public class CommentGatewayDto {
    private long id;
    @NotBlank
    private String text;
    private String authorName;
    private LocalDateTime created;

    public CommentGatewayDto() {

    }
}
