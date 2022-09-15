package ru.practicum.shareit.requests.dto;

import lombok.*;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

@Getter
@Setter
@ToString
@EqualsAndHashCode
@AllArgsConstructor
public class ItemRequestShortGatewayDto {
    @NotNull
    @NotBlank
    private String description;

    public ItemRequestShortGatewayDto() {

    }
}
