package ru.practicum.shareit.exception.bookings;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.BAD_REQUEST)
public class BookingValidateException extends RuntimeException {
    public BookingValidateException(String message) {
        super(message);
    }
}
