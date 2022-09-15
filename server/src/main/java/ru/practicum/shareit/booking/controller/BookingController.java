package ru.practicum.shareit.booking.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.dto.BookingShortDto;
import ru.practicum.shareit.booking.service.BookingService;

import java.util.List;

@RestController
@RequestMapping(path = "/bookings")
@Slf4j
public class BookingController {
    private final BookingService service;

    @Autowired
    public BookingController(BookingService service) {
        this.service = service;
    }

    @PostMapping //создает новое бронирование
    public BookingDto createNewBooking(@RequestHeader("X-Sharer-User-Id") long userId,
                                       @RequestBody BookingShortDto shortDto) {
        return service.createNewBooking(userId, shortDto);
    }

    @PatchMapping("/{bookingId}")//обновляет бронирование
    public BookingDto updateBooking(@RequestHeader("X-Sharer-User-Id") long userId, @PathVariable long bookingId,
                                    @RequestParam(name = "approved") Boolean isApproved) {
        return service.updateBooking(userId, bookingId, isApproved);
    }

    @GetMapping("/{bookingId}") //возвращает бронирование по Id
    public BookingDto getBookingById(@RequestHeader("X-Sharer-User-Id") long userId, @PathVariable Long bookingId) {
        return service.getBookingById(userId, bookingId);
    }

    @GetMapping //возвращает все бронирования для создателя бронирований
    public List<BookingDto> getAllForBooker(@RequestHeader("X-Sharer-User-Id") long userId,
                                            @RequestParam(name = "state") String state,
                                            @RequestParam(name = "from") int from,
                                            @RequestParam(name = "size") int size) {
        return service.getAllForBooker(userId, state, from, size);
    }

    @GetMapping("/owner") //возвращает все бронирования для собственника вещей
    public List<BookingDto> getAllForOwner(@RequestHeader("X-Sharer-User-Id") long userId,
                                           @RequestParam(name = "state") String state,
                                           @RequestParam(name = "from") int from,
                                           @RequestParam(name = "size") int size) {
        return service.getAllForOwner(userId, state, from, size);
    }
}
