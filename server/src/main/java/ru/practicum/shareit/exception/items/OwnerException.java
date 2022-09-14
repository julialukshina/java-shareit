package ru.practicum.shareit.exception.items;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.NOT_FOUND)
public class OwnerException extends RuntimeException {
    public OwnerException(String message) {
        super(message);
    }
}
