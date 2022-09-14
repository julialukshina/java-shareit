package ru.practicum.shareit.booking.service;

import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.dto.BookingShortDto;

import java.util.List;

public interface BookingService {
    BookingDto createNewBooking(long userId, BookingShortDto shortDto);

    BookingDto updateBooking(long userId, long bookingId, Boolean isApproved);


    BookingDto getBookingById(long userId, Long bookingId);

    List<BookingDto> getAllForBooker(long userId, String state, int from, int size);

    List<BookingDto> getAllForOwner(long userId, String state, int from, int size);
}
