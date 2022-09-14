package ru.practicum.shareit.booking.mapper;

import org.springframework.stereotype.Component;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.dto.BookingDtoForItemDto;

@Component
public class BookingForItemDtoMapper {
    public BookingDtoForItemDto toBookingForItemDto(BookingDto bookingDto) {
        return BookingDtoForItemDto.builder()
                .id(bookingDto.getId())
                .start(bookingDto.getStart())
                .end(bookingDto.getEnd())
                .itemId(bookingDto.getItem().getId())
                .bookerId(bookingDto.getBooker().getId())
                .status(bookingDto.getStatus())
                .build();
    }
}
