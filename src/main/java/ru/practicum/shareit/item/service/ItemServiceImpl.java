package ru.practicum.shareit.item.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.exception.items.ItemNotFoundException;
import ru.practicum.shareit.exception.items.ItemValidationException;
import ru.practicum.shareit.exception.items.NotOwnerException;
import ru.practicum.shareit.exception.users.UserNotFoundException;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.mapper.ItemMapper;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.repository.ItemRepository;
import ru.practicum.shareit.user.repository.UserRepository;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@Slf4j
public class ItemServiceImpl implements ItemService {
    private long id = 1;
    private final ItemRepository repository;
    private final UserRepository userRepository;
    private final Map<Long, List<Item>> usersItems = new HashMap<>();

    @Lazy
    @Autowired
    private ItemMapper mapper;

    @Autowired
    public ItemServiceImpl(ItemRepository repository, UserRepository userRepository) {
        this.repository = repository;
        this.userRepository = userRepository;
    }

    @Override
    public List<ItemDto> getAllItems() {
        return new ArrayList<>(repository.getAllItems().values()).stream()
                .map(mapper::toItemDto)
                .collect(Collectors.toList());
    }

    @Override
    public ItemDto createNewItem(long userId, ItemDto itemDto) {
        if (!userRepository.getAllUsers().containsKey(userId)) {
            throw new UserNotFoundException("This user is not found");
        }
        itemDto.setId(generateId());
        itemDto.setOwnerId(userId);
        repository.addItem(mapper.toItem(itemDto));
        if (usersItems.containsKey(userId)) {
            usersItems.get(userId).add(mapper.toItem(itemDto));
        } else {
            List<Item> items = new ArrayList<>();
            items.add(mapper.toItem(itemDto));
            usersItems.put(userId, items);
        }
        log.info("The new item has been created: {}", mapper.toItem(itemDto));
        return itemDto;
    }

    @Override
    public ItemDto updateItem(long userId, Long itemId, ItemDto item) {
        itemValidation(itemId);
        if (userId != repository.getItemById(itemId).getOwner().getId()) {
            throw new NotOwnerException("The item can be updated only by the owner");
        }
        if (item.getName() != null) {
            if (item.getName().isBlank()) {
                throw new ItemValidationException("The name can not be empty");
            }
        }
        if (item.getDescription() != null) {
            if (item.getDescription().isBlank()) {
                throw new ItemValidationException("The name can not be empty");
            }
        }
        usersItems.get(userId).remove(repository.getItemById(itemId));
        repository.updateItem(itemId, item.getName(), item.getDescription(), item.getAvailable());
        usersItems.get(userId).add(repository.getItemById(itemId));
        log.info("The item with id {} updated", itemId);
        return getItemById(itemId);
    }


    @Override
    public ItemDto getItemById(Long itemId) {
        itemValidation(itemId);
        return mapper.toItemDto(repository.getItemById(itemId));
    }

    @Override
    public void deleteItem(Long userId, Long itemId) {
        itemValidation(itemId);
        if (userId != repository.getItemById(itemId).getOwner().getId()) {
            throw new NotOwnerException("The item can be deleted only by the owner");
        }
        usersItems.get(repository.getItemById(itemId).getOwner().getId()).remove(repository.getItemById(itemId));
        repository.deleteItem(itemId);
        log.info("The item with id {} deleted", itemId);
    }

    @Override
    public List<ItemDto> getSearchableItem(String text) {
        if (text.isBlank()) {
            return new ArrayList<>();
        }
        return getAllItems().stream()
                .filter(ItemDto::getAvailable)
                .filter(itemDto -> itemDto.getName().toLowerCase().contains(text.toLowerCase())
                        || itemDto.getDescription().toLowerCase().contains(text.toLowerCase()))
                .collect(Collectors.toList());
    }

    @Override
    public List<ItemDto> getItemsOfUser(long userId) {
        if (!usersItems.containsKey(userId)) {
            return new ArrayList<>();
        }
        return usersItems.get(userId).stream()
                .map(mapper::toItemDto)
                .collect(Collectors.toList());
    }

    private long generateId() { //метод генерации id
        return id++;
    }

    private void itemValidation(Long itemId) {
        if (!repository.getAllItems().containsKey(itemId)) {
            throw new ItemNotFoundException("This item is not found");
        }
    }

    public void clear() {
        repository.clear();
        usersItems.clear();
        id = 1;
    }

    @Override
    public Item getItem(long id) {
        return repository.getItemById(id);
    }
}
