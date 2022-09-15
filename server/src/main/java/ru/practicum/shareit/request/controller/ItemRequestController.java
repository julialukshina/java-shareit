package ru.practicum.shareit.request.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.request.dto.ItemRequestShortDto;
import ru.practicum.shareit.request.service.ItemRequestService;

import java.util.List;

@RestController
@RequestMapping(path = "/requests")
@Slf4j
public class ItemRequestController {
    private final ItemRequestService service;

    @Autowired
    public ItemRequestController(ItemRequestService service) {
        this.service = service;
    }

    @PostMapping //создает новый запрос
    public ItemRequestDto createNewItemRequest(@RequestHeader("X-Sharer-User-Id") long userId,
                                               @RequestBody ItemRequestShortDto itemRequestShortDto) {
        return service.createNewItemRequest(userId, itemRequestShortDto);
    }

    @GetMapping //возвращает список запросов пользователя
    public List<ItemRequestDto> getItemRequestsOfUser(@RequestHeader("X-Sharer-User-Id") long userId) {
        return service.getItemRequestsOfUser(userId);
    }

    @GetMapping("/all") //возвращает список всех чужих запросов
    public List<ItemRequestDto> getAllItemRequests(@RequestHeader("X-Sharer-User-Id") long userId,
                                                   @RequestParam(name = "from") int from,
                                                   @RequestParam(name = "size") int size) {
        return service.getAllItemRequests(userId, from, size);
    }

    @GetMapping("/{requestId}") //возвращает запрос по id
    public ItemRequestDto getItemRequestByItemRequestId(@RequestHeader("X-Sharer-User-Id") long userId,
                                                        @PathVariable long requestId) {
        return service.getItemRequestByItemRequestId(userId, requestId);
    }
}
