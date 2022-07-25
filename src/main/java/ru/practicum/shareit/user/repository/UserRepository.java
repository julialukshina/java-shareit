package ru.practicum.shareit.user.repository;

import org.springframework.stereotype.Repository;
import ru.practicum.shareit.user.model.User;

import java.util.HashMap;
import java.util.Map;

@Repository
public class UserRepository {
    private final Map<Long, User> users = new HashMap<>();

    public void addUser(User user) {
        users.put(user.getId(), user);
    }

    public void updateUser(Long userId, String name, String email) {
        if (name != null) {
            users.get(userId).setName(name);
        }
        if (email != null) {
            users.get(userId).setEmail(email);
        }
    }

    public void deleteUser(Long id) {
        users.remove(id);
    }

    public Map<Long, User> getAllUsers() {
        return users;
    }

    public User getUserById(Long id) {
        return users.get(id);
    }

    public void clear() {
        users.clear();
    }
}
