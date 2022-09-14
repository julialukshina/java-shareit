package ru.practicum.shareit.request.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.request.dto.ItemRequestShortDto;
import ru.practicum.shareit.request.service.ItemRequestService;

import javax.validation.ConstraintViolationException;
import javax.validation.Valid;
import javax.validation.constraints.Positive;
import javax.validation.constraints.PositiveOrZero;
import java.util.List;

@RestController
@RequestMapping(path = "/requests")
@Validated
@Slf4j
public class ItemRequestController {
    private final ItemRequestService service;

    @Autowired
    public ItemRequestController(ItemRequestService service) {
        this.service = service;
    }

    @PostMapping //создает новый запрос
    public ItemRequestDto createNewItemRequest(@RequestHeader("X-Sharer-User-Id") long userId,
                                               @Valid @RequestBody ItemRequestShortDto itemRequestShortDto) {
        return service.createNewItemRequest(userId, itemRequestShortDto);
    }

    @GetMapping //возвращает список запросов пользователя
    public List<ItemRequestDto> getItemRequestsOfUser(@RequestHeader("X-Sharer-User-Id") long userId) {
        return service.getItemRequestsOfUser(userId);
    }

    @GetMapping("/all") //возвращает список всех чужих запросов
    public List<ItemRequestDto> getAllItemRequests(@RequestHeader("X-Sharer-User-Id") long userId,
                                                   @RequestParam(name = "from", defaultValue = "0")
                                                   @PositiveOrZero int from, @RequestParam(name = "size",
            defaultValue = "10") @Positive int size) {
        return service.getAllItemRequests(userId, from, size);
    }

    @GetMapping("/{requestId}") //возвращает запрос по id
    public ItemRequestDto getItemRequestByItemRequestId(@RequestHeader("X-Sharer-User-Id") long userId, @PathVariable long requestId) {
        return service.getItemRequestByItemRequestId(userId, requestId);
    }

    @ExceptionHandler(ConstraintViolationException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ResponseEntity<String> handleConstraintViolationException(ConstraintViolationException e) {
        return new ResponseEntity<>("not valid due to validation error: " + e.getMessage(), HttpStatus.BAD_REQUEST);
    }
}
