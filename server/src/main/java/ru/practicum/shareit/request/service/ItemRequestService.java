package ru.practicum.shareit.request.service;


import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.request.dto.ItemRequestShortDto;
import ru.practicum.shareit.request.model.ItemRequest;

import java.util.List;

public interface ItemRequestService {
    ItemRequestDto createNewItemRequest(long userId, ItemRequestShortDto itemRequestShortDto);

    List<ItemRequestDto> getItemRequestsOfUser(long userId);

    public ItemRequest getItemRequestById(long id);

    List<ItemRequestDto> getAllItemRequests(long userId, int from, int size);

    ItemRequestDto getItemRequestByItemRequestId(long userId, long requestId);
}
