package ru.practicum.shareit.requests.service;


import ru.practicum.shareit.requests.dto.ItemRequestDto;
import ru.practicum.shareit.requests.dto.ItemRequestShortDto;
import ru.practicum.shareit.requests.model.ItemRequest;

import java.util.List;

public interface ItemRequestService {
    ItemRequestDto createNewItemRequest(long userId, ItemRequestShortDto itemRequestShortDto);

    List<ItemRequestDto> getItemRequestsOfUser(long userId);

    public ItemRequest getItemRequestById(long id);

    List<ItemRequestDto> getAllItemRequests(long userId, int from, int size);

    ItemRequestDto getItemRequestByItemRequestId(long userId, long requestId);
}
