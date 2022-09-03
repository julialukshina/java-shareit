package ru.practicum.shareit.booking.mapper;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Component;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.item.mapper.ItemMapper;
import ru.practicum.shareit.user.mapper.UserMapper;

@Component
public class BookingMapper {
    @Lazy
    @Autowired
    private ItemMapper mapper;

    public BookingDto toBookingDto(Booking booking) {
        return new BookingDto(booking.getId(),
                booking.getStart(),
                booking.getEnd(),
                mapper.toItemDto(booking.getItem()),
                UserMapper.toUserDto(booking.getBooker()),
                booking.getStatus());
    }

    public Booking toBooking(BookingDto dto) {
        return new Booking(dto.getId(),
                dto.getStart(),
                dto.getEnd(),
                mapper.toItem(dto.getItem()),
                UserMapper.toUser(dto.getBooker()),
                dto.getStatus());
    }
}
