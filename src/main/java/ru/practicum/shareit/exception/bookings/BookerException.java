package ru.practicum.shareit.exception.bookings;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.NOT_FOUND)
public class BookerException extends RuntimeException {
    public BookerException(String message) {
        super(message);
    }
}
