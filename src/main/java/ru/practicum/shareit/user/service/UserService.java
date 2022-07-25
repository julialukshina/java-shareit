package ru.practicum.shareit.user.service;

import ru.practicum.shareit.user.dto.UserDto;

import java.util.List;
import java.util.Map;

public interface UserService {
    List<UserDto> getAllUsers();

    UserDto createNewUser(UserDto user);

    UserDto updateUser(Long userId, Map<String, String> user);

    void deleteUser(Long id);

    UserDto getUserById(Long id);
}
