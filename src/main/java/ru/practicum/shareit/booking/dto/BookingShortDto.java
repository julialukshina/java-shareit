package ru.practicum.shareit.booking.dto;

import lombok.*;

import javax.validation.constraints.FutureOrPresent;
import java.time.LocalDateTime;

@Getter
@Setter
@ToString
@EqualsAndHashCode
@AllArgsConstructor
public class BookingShortDto {
    @FutureOrPresent
    private LocalDateTime start;
    private LocalDateTime end;
    private long itemId;

    public BookingShortDto() {

    }
}
