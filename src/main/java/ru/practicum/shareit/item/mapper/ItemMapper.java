package ru.practicum.shareit.item.mapper;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Component;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.service.ItemService;
import ru.practicum.shareit.user.service.UserService;

@Lazy
@Component
public class ItemMapper {
    private final UserService userService;
    private final ItemService itemService;

    @Autowired
    public ItemMapper(UserService userService, ItemService itemService) {
        this.userService = userService;
        this.itemService = itemService;
    }

    public ItemDto toItemDto(Item item) {
        return new ItemDto(item.getId(),
                item.getName(),
                item.getDescription(),
                item.getAvailable(),
                item.getOwner().getId(),
                item.getRequest() != null ? item.getRequest().getId() : null
        );
    }

    public Item toItem(ItemDto itemDto) {
        return new Item(itemDto.getId(),
                itemDto.getName(),
                itemDto.getDescription(),
                itemDto.getAvailable(),
                userService.getUser(itemDto.getOwnerId()),
                itemDto.getRequest() != null ? itemService.getItem(itemDto.getId()).getRequest() : null
        );
    }
}
