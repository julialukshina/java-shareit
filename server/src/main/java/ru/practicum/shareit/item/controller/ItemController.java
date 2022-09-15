package ru.practicum.shareit.item.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.item.dto.CommentDto;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.service.ItemService;

import java.util.List;

@RestController
@RequestMapping("/items")
@Slf4j
public class ItemController {
    private final ItemService service;

    @Autowired
    public ItemController(ItemService service) {
        this.service = service;
    }

    @PostMapping //создает новую вещь
    public ItemDto createNewItem(@RequestHeader("X-Sharer-User-Id") long userId, @RequestBody ItemDto itemDto) {
        return service.createNewItem(userId, itemDto);
    }

    @PatchMapping("/{itemId}")//обновляет вещь
    public ItemDto updateItem(@RequestHeader("X-Sharer-User-Id") long userId, @PathVariable Long itemId, @RequestBody ItemDto itemDto) {
        return service.updateItem(userId, itemId, itemDto);
    }

    @GetMapping("/{itemId}") //возвращает вещь по Id
    public ItemDto getItemById(@RequestHeader("X-Sharer-User-Id") long userId, @PathVariable Long itemId) {
        return service.getItemById(userId, itemId);
    }

    @DeleteMapping("/{itemId}") //удаляет вещь
    public void deleteItem(@RequestHeader("X-Sharer-User-Id") long userId, @PathVariable Long itemId) {
        service.deleteItem(userId, itemId);
    }

    @GetMapping("/search") //поиск вещей
    public List<ItemDto> getSearchableItem(@RequestParam String text,
                                           @RequestParam(name = "from") int from,
                                           @RequestParam(name = "size") int size) { //возвращаем список искомых вещей
        return service.getSearchableItem(text, from, size);
    }

    @GetMapping //возвращает список вещей пользователя
    public List<ItemDto> getItemsOfUser(@RequestHeader("X-Sharer-User-Id") long userId,
                                        @RequestParam(name = "from") int from,
                                        @RequestParam(name = "size") int size) {
        return service.getItemsOfUser(userId, from, size);
    }

    @PostMapping("/{itemId}/comment") //добавление комментария
    public CommentDto createNewComment(@RequestHeader("X-Sharer-User-Id") long userId, @PathVariable long itemId,
                                       @RequestBody CommentDto commentDto) {
        return service.createNewComment(userId, itemId, commentDto);
    }
}