package ru.practicum.shareit.item.service;

import ru.practicum.shareit.item.dto.ItemDto;

import java.util.List;
import java.util.Map;

public interface ItemService {
    List<ItemDto> getAllItems();

    ItemDto createNewItem(long userId, ItemDto itemDto);

    ItemDto updateItem(long userId, Long itemId, Map<String, String> item);

    ItemDto getItemById(Long itemId);

    void deleteItem(Long userId, Long itemId);

    List<ItemDto> getSearchableItem(String text);

    List<ItemDto> getItemsOfUser(long userId);
}
