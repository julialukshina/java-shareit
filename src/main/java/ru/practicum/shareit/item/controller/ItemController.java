package ru.practicum.shareit.item.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.OnCreate;
import ru.practicum.shareit.item.service.ItemService;

import javax.validation.ConstraintViolationException;
import javax.validation.Valid;
import java.util.List;

@RestController
@RequestMapping("/items")
@Validated
@Slf4j
public class ItemController {
    private final ItemService service;

    @Autowired
    public ItemController(ItemService service) {
        this.service = service;
    }

    @Validated(OnCreate.class)
    @PostMapping //создает новую вещь
    public ItemDto createNewItem(@RequestHeader("X-Sharer-User-Id") long userId, @Valid @RequestBody ItemDto itemDto) {
        return service.createNewItem(userId, itemDto);
    }

    @PatchMapping("/{itemId}")//обновляет вещь
    public ItemDto updateItem(@RequestHeader("X-Sharer-User-Id") long userId, @PathVariable Long itemId, @RequestBody ItemDto itemDto) {
        return service.updateItem(userId, itemId, itemDto);
    }

    @GetMapping("/{itemId}") //возвращает вещь по Id
    public ItemDto getItemById(@PathVariable Long itemId) {
        return service.getItemById(itemId);
    }

    @DeleteMapping("/{itemId}") //удаляет вещь
    public void deleteItem(@RequestHeader("X-Sharer-User-Id") long userId, @PathVariable Long itemId) {
        service.deleteItem(userId, itemId);
    }

    @GetMapping("/search") //поиск вещей
    public List<ItemDto> getSearchableItem(@RequestParam String text) { //возвращаем список искомых вещей
        return service.getSearchableItem(text);
    }

    @GetMapping //возвращает список вещей пользователя
    public List<ItemDto> getItemsOfUser(@RequestHeader("X-Sharer-User-Id") long userId) {
        return service.getItemsOfUser(userId);
    }

    @ExceptionHandler(ConstraintViolationException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ResponseEntity<String> handleConstraintViolationException(ConstraintViolationException e) {
        return new ResponseEntity<>("not valid due to validation error: " + e.getMessage(), HttpStatus.BAD_REQUEST);
    }
}