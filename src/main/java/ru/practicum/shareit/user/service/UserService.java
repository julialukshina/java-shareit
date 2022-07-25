package ru.practicum.shareit.user.service;

import ru.practicum.shareit.user.dto.UserDto;

import java.util.List;
import java.util.Map;

public interface UserService {
    public List<UserDto> getAllUsers();

    public UserDto createNewUser(UserDto user);

    public UserDto updateUser(Long userId, Map<String, String> user);

    public void deleteUser(Long id);

    UserDto getUserById(Long id);
}
