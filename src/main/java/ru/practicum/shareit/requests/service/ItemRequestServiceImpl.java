package ru.practicum.shareit.requests.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.MyPageable;
import ru.practicum.shareit.exception.itemRequests.ItemRequestNotFoundException;
import ru.practicum.shareit.exception.users.UserNotFoundException;
import ru.practicum.shareit.item.mapper.ItemMapper;
import ru.practicum.shareit.item.repository.ItemRepository;
import ru.practicum.shareit.requests.dto.ItemRequestDto;
import ru.practicum.shareit.requests.dto.ItemRequestShortDto;
import ru.practicum.shareit.requests.mapper.ItemRequestMapper;
import ru.practicum.shareit.requests.model.ItemRequest;
import ru.practicum.shareit.requests.repository.ItemRequestRepository;
import ru.practicum.shareit.user.repository.UserRepository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@Slf4j
public class ItemRequestServiceImpl implements ItemRequestService {
    @Lazy
    @Autowired
    private ItemRequestMapper mapper;
    @Lazy
    @Autowired
    private ItemMapper itemMapper;
    private final ItemRequestRepository repository;
    private final UserRepository userRepository;
    private final ItemRepository itemRepository;

    @Autowired
    public ItemRequestServiceImpl(ItemRequestRepository repository, UserRepository userRepository, ItemRepository itemRepository) {
        this.repository = repository;
        this.userRepository = userRepository;
        this.itemRepository = itemRepository;
    }

    @Override
    public ItemRequestDto createNewItemRequest(long userId, ItemRequestShortDto itemRequestShortDto) {
        userValidation(userId);
        ItemRequestDto dto = ItemRequestDto.builder()
                .description(itemRequestShortDto.getDescription())
                .requesterId(userId)
                .created(LocalDateTime.now())
                .build();
        dto.setId(repository.save(mapper.toItemRequest(dto)).getId());
        log.info("The new itemRequest has been created: {}", dto);
        return dto;
    }

    @Override
    public List<ItemRequestDto> getItemRequestsOfUser(long userId) {
        userValidation(userId);
        return repository.findAllByRequesterId(userId).stream()
                .map(mapper::toItemRequestDto)
                .map(this::fillItemsToItemRequestDto)
                .sorted((o1, o2) -> (int) (o2.getCreated().compareTo(o1.getCreated())))
                .collect(Collectors.toList());

    }

    @Override
    public List<ItemRequestDto> getAllItemRequests(long userId, int from, int size) {
        userValidation(userId);
        Pageable pageable = MyPageable.of(from, size, Sort.by(Sort.Direction.DESC, "created"));
        return repository.findAllByRequesterIdNot(userId, pageable).stream()
                .map(mapper::toItemRequestDto)
                .map(this::fillItemsToItemRequestDto)
                .collect(Collectors.toList());
    }

    @Override
    public ItemRequestDto getItemRequestByItemRequestId(long userId, long requestId) {
        userValidation(userId);
        if (repository.findById(requestId).isEmpty()) {
            throw new ItemRequestNotFoundException("The itemRequest is not found");
        }
        return fillItemsToItemRequestDto(mapper.toItemRequestDto(repository.findById(requestId).get()));
    }

    private void userValidation(long userId) {
        if (userRepository.findById(userId).isEmpty()) {
            throw new UserNotFoundException("This user is not found");
        }
    }

    private ItemRequestDto fillItemsToItemRequestDto(ItemRequestDto itemRequestDto) {
        if (!itemRepository.findByRequestId(itemRequestDto.getId()).isEmpty()) {
            itemRequestDto.setItems(itemRepository.findByRequestId(itemRequestDto.getId()).stream()
                    .map(itemMapper::toItemDto)
                    .collect(Collectors.toList()));
        }
        return itemRequestDto;
    }

    @Override
    public ItemRequest getItemRequestById(long id) {
        return repository.findById(id).get();
    }
}
