package ru.practicum.shareit.booking.dto;

import lombok.*;
import ru.practicum.shareit.booking.model.BookingStatus;

import java.time.LocalDateTime;

@Getter
@Setter
@ToString
@EqualsAndHashCode
@Builder
public class BookingDtoForItemDto {
    private long id;
    private LocalDateTime start;
    private LocalDateTime end;
    private long itemId;
    private long bookerId;
    private BookingStatus status;
}