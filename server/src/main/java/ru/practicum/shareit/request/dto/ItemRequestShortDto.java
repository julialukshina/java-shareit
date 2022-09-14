package ru.practicum.shareit.request.dto;

import lombok.*;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

@Getter
@Setter
@ToString
@EqualsAndHashCode
@AllArgsConstructor
public class ItemRequestShortDto {
    @NotNull
    @NotBlank
    private String description;

    public ItemRequestShortDto() {

    }
}
