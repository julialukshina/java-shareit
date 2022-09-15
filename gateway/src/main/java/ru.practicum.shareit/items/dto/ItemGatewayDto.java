package ru.practicum.shareit.items.dto;

import lombok.*;
import ru.practicum.shareit.OnCreate;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.util.List;

@Getter
@Setter
@ToString
@EqualsAndHashCode
@AllArgsConstructor
public class ItemGatewayDto {
    private long id;
    @NotBlank(groups = OnCreate.class)
    private String name;
    @NotBlank(groups = OnCreate.class)
    private String description;
    @NotNull(groups = OnCreate.class)
    private Boolean available;
    private long ownerId;
    private Long requestId;
    private BookingDtoForItemGatewayDto lastBooking;
    private BookingDtoForItemGatewayDto nextBooking;
    private List<CommentGatewayDto> comments;

}
