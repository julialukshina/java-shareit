package ru.practicum.shareit.items;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.OnCreate;
import ru.practicum.shareit.items.dto.CommentGatewayDto;
import ru.practicum.shareit.items.dto.ItemGatewayDto;

import javax.validation.ConstraintViolationException;
import javax.validation.Valid;
import javax.validation.constraints.Positive;
import javax.validation.constraints.PositiveOrZero;

@RestController
@RequestMapping(path = "/items")
@RequiredArgsConstructor
@Slf4j
@Validated
public class ItemGatewayController {
    private final ItemClient itemClient;

    @Validated(OnCreate.class)
    @PostMapping
    public ResponseEntity<Object> bookItem(@RequestHeader("X-Sharer-User-Id") long userId,
                                           @RequestBody @Valid ItemGatewayDto requestDto) {
        log.info("Creating item {}, userId={}", requestDto, userId);
        return itemClient.createItem(userId, requestDto);
    }

    @PatchMapping("/{itemId}")//обновляет вещь
    public ResponseEntity<Object> patchItem(@RequestHeader("X-Sharer-User-Id") long userId, @PathVariable Long itemId, @RequestBody ItemGatewayDto itemGatewayDto) {
        return itemClient.updateItem(userId, itemId, itemGatewayDto);
    }

    @GetMapping("/{itemId}") //возвращает вещь по Id
    public ResponseEntity<Object> getById(@RequestHeader("X-Sharer-User-Id") long userId, @PathVariable Long itemId) {
        return itemClient.getItemById(userId, itemId);
    }

    @GetMapping("/search") //поиск вещей
    public ResponseEntity<Object> getSearchItem(@RequestParam(required = false) String text,
                                                @PositiveOrZero @RequestParam(name = "from", defaultValue = "0") int from,
                                                @Positive @RequestParam(name = "size", defaultValue = "10") int size) { //возвращаем список искомых вещей
        return itemClient.getSearchableItem(text, from, size);
    }

    @GetMapping //возвращает список вещей пользователя
    public ResponseEntity<Object> getItemsOfOwner(@RequestHeader("X-Sharer-User-Id") long userId,
                                                  @PositiveOrZero @RequestParam(name = "from", defaultValue = "0") int from,
                                                  @Positive @RequestParam(name = "size", defaultValue = "10") int size) {
        return itemClient.getItemsOfUser(userId, from, size);
    }

    @PostMapping("/{itemId}/comment") //добавление комментария
    public ResponseEntity<Object> createComment(@RequestHeader("X-Sharer-User-Id") long userId, @PathVariable long itemId,
                                                @Valid @RequestBody CommentGatewayDto commentGatewayDto) {
        return itemClient.createNewComment(userId, itemId, commentGatewayDto);
    }

    @ExceptionHandler(ConstraintViolationException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ResponseEntity<String> handleConstraintViolationException(ConstraintViolationException e) {
        return new ResponseEntity<>("not valid due to validation error: " + e.getMessage(), HttpStatus.BAD_REQUEST);
    }
}
