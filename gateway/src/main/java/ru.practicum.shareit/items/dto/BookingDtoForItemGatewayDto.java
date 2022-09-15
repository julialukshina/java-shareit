package ru.practicum.shareit.items.dto;

import lombok.*;

import java.time.LocalDateTime;

@Getter
@Setter
@ToString
@EqualsAndHashCode
@Builder
public class BookingDtoForItemGatewayDto {
    private long id;
    private LocalDateTime start;
    private LocalDateTime end;
    private long itemId;
    private long bookerId;
    private BookingGatewayStatus status;
}