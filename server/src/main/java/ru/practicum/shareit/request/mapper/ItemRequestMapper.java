package ru.practicum.shareit.request.mapper;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Component;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.request.model.ItemRequest;
import ru.practicum.shareit.user.service.UserService;

import java.util.ArrayList;

@Component
@Lazy
public class ItemRequestMapper {
    private final UserService userService;

    @Autowired
    public ItemRequestMapper(UserService userService) {
        this.userService = userService;
    }

    public ItemRequestDto toItemRequestDto(ItemRequest request) {
        return new ItemRequestDto(request.getId(),
                request.getDescription(),
                request.getRequester().getId(),
                request.getCreated(),
                new ArrayList<ItemDto>());
    }

    public ItemRequest toItemRequest(ItemRequestDto dto) {
        return new ItemRequest(dto.getId(),
                dto.getDescription(),
                userService.getUser(dto.getRequesterId()),
                dto.getCreated()
        );
    }
}
