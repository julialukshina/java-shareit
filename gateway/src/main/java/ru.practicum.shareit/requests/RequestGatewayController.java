package ru.practicum.shareit.requests;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.requests.dto.ItemRequestShortGatewayDto;

import javax.validation.ConstraintViolationException;
import javax.validation.Valid;
import javax.validation.constraints.Positive;
import javax.validation.constraints.PositiveOrZero;

@RestController
@RequestMapping(path = "/requests")
@RequiredArgsConstructor
@Slf4j
@Validated
public class RequestGatewayController {
    private final RequestClient requestClient;

    @PostMapping //создает новый запрос
    public ResponseEntity<Object> createNewRequest(@RequestHeader("X-Sharer-User-Id") long userId,
                                                   @Valid @RequestBody ItemRequestShortGatewayDto itemRequestShortGatewayDto) {
        return requestClient.createNewItemRequest(userId, itemRequestShortGatewayDto);
    }

    @GetMapping //возвращает список запросов пользователя
    public ResponseEntity<Object> getRequestsOfUser(@RequestHeader("X-Sharer-User-Id") long userId) {
        return requestClient.getItemRequestsOfUser(userId);
    }

    @GetMapping("/all") //возвращает список всех чужих запросов
    public ResponseEntity<Object> getAllRequests(@RequestHeader("X-Sharer-User-Id") long userId,
                                                 @PositiveOrZero @RequestParam(name = "from", defaultValue = "0") int from,
                                                 @Positive @RequestParam(name = "size", defaultValue = "10") int size) {
        return requestClient.getAllItemRequests(userId, from, size);
    }

    @GetMapping("/{requestId}") //возвращает запрос по id
    public ResponseEntity<Object> getRequestByItemRequestId(@RequestHeader("X-Sharer-User-Id") long userId,
                                                            @PathVariable long requestId) {
        return requestClient.getItemRequestByItemRequestId(userId, requestId);
    }

    @ExceptionHandler(ConstraintViolationException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ResponseEntity<String> handleConstraintViolationException(ConstraintViolationException e) {
        return new ResponseEntity<>("not valid due to validation error: " + e.getMessage(), HttpStatus.BAD_REQUEST);
    }
}
