package ru.practicum.shareit.request.dto;

import lombok.*;

@Getter
@Setter
@ToString
@EqualsAndHashCode
@AllArgsConstructor
public class ItemRequestShortDto {
    private String description;

    public ItemRequestShortDto() {

    }
}
