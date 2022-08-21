package ru.practicum.shareit.item.dto;

import lombok.*;
import ru.practicum.shareit.OnCreate;
import ru.practicum.shareit.booking.dto.BookingDtoForItemDto;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.util.List;

@Getter
@Setter
@ToString
@EqualsAndHashCode
@AllArgsConstructor
public class ItemDto {
    private long id;
    @NotBlank(groups = OnCreate.class)
    private String name;
    @NotBlank(groups = OnCreate.class)
    private String description;
    @NotNull(groups = OnCreate.class)
    private Boolean available;
    private long ownerId;
    //    private Long request;
    private BookingDtoForItemDto lastBooking;
    private BookingDtoForItemDto nextBooking;
    private List<CommentDto> comments;

}
