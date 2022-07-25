package ru.practicum.shareit.item.repository;

import org.springframework.stereotype.Repository;
import ru.practicum.shareit.item.model.Item;

import java.util.HashMap;
import java.util.Map;

@Repository
public class ItemRepository {
    private Map<Long, Item> items = new HashMap<>();

    public void addItem(Item item) {
        items.put(item.getId(), item);
    }

    public void updateItem(Long itemId, String name, String description, Boolean available) {
        if (name != null) {
            items.get(itemId).setName(name);
        }
        if (description != null) {
            items.get(itemId).setDescription(description);
        }
        if (available != null) {
            items.get(itemId).setAvailable(available);
        }
    }

    public void deleteItem(Long id) {
        items.remove(id);
    }

    public Map<Long, Item> getAllItems() {
        return items;
    }

    public Item getItemById(Long id) {
        return items.get(id);
    }

    public void clear() {
        items.clear();
    }
}
