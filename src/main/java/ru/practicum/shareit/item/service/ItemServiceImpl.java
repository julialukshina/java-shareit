package ru.practicum.shareit.item.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
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

    @Autowired
    public ItemServiceImpl(ItemRepository repository, UserRepository userRepository) {
        this.repository = repository;
        this.userRepository = userRepository;
    }

    @Override
    public List<ItemDto> getAllItems() {
        return new ArrayList<>(repository.getAllItems().values()).stream()
                .map(ItemMapper::toItemDto)
                .collect(Collectors.toList());
    }

    @Override
    public ItemDto createNewItem(long userId, ItemDto itemDto) {
        if (!userRepository.getAllUsers().containsKey(userId)) {
            throw new UserNotFoundException("This user is not found");
        }
        itemDto.setId(generateId());
        itemDto.setOwnerId(userId);
        repository.addItem(ItemMapper.toItem(itemDto));
        if (usersItems.containsKey(userId)) {
            usersItems.get(userId).add(ItemMapper.toItem(itemDto));
        } else {
            List<Item> items = new ArrayList<>();
            items.add(ItemMapper.toItem(itemDto));
            usersItems.put(userId, items);
        }
        log.info("The new item has been created: {}", ItemMapper.toItem(itemDto));
        return itemDto;
    }

    @Override
    public ItemDto updateItem(long userId, Long itemId, Map<String, String> item) {
        itemValidation(itemId);
        if (userId != repository.getItemById(itemId).getOwnerId()) {
            throw new NotOwnerException("The item can be updated only by the owner");
        }
        usersItems.get(userId).remove(repository.getItemById(itemId));
        String name = null;
        String description = null;
        Boolean available = null;
        if (item.containsKey("name")) {
            if (!item.get("name").isBlank()) {
                name = item.get("name");
            } else {
                throw new ItemValidationException("The name can't be empty");
            }
        }
        if (item.containsKey("description")) {
            if (!item.get("description").isBlank()) {
                description = item.get("description");
            } else {
                throw new ItemValidationException("The description can't be empty");
            }
        }
        if (item.containsKey("available")) {
            available = Boolean.valueOf(item.get("available"));
        }
        repository.updateItem(itemId, name, description, available);
        usersItems.get(userId).add(repository.getItemById(itemId));
        log.info("The item with id {} updated", itemId);
        return getItemById(itemId);
    }

    @Override
    public ItemDto getItemById(Long itemId) {
        itemValidation(itemId);
        return ItemMapper.toItemDto(repository.getItemById(itemId));
    }

    @Override
    public void deleteItem(Long userId, Long itemId) {
        itemValidation(itemId);
        if (userId != repository.getItemById(itemId).getOwnerId()) {
            throw new NotOwnerException("The item can be deleted only by the owner");
        }
        usersItems.get(repository.getItemById(itemId).getOwnerId()).remove(repository.getItemById(itemId));
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
                .map(ItemMapper::toItemDto)
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
}
