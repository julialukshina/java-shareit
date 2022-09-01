package ru.practicum.shareit.requests.dto;

import lombok.*;
import ru.practicum.shareit.item.dto.ItemDto;

import java.time.LocalDateTime;
import java.util.List;

@Getter
@Setter
@ToString
@EqualsAndHashCode
@AllArgsConstructor
@Builder
public class ItemRequestDto {
    private long id;
    private String description;
    private long requesterId;
    private LocalDateTime created;
    private List<ItemDto> items;

}
