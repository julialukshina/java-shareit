package ru.practicum.shareit.user.service;

import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.model.User;

import java.util.List;

public interface UserService {
    List<UserDto> getAllUsers();

    UserDto createNewUser(UserDto user);

    UserDto updateUser(Long userId, UserDto user);

    void deleteUser(Long id);

    UserDto getUserById(Long id);

    User getUser(Long id);
}
