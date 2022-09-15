package ru.practicum.shareit.booking.dto;

import lombok.*;

import java.time.LocalDateTime;

@Getter
@Setter
@ToString
@EqualsAndHashCode
@AllArgsConstructor
public class BookingShortDto {
    private LocalDateTime start;
    private LocalDateTime end;
    private long itemId;

    public BookingShortDto() {

    }
}
