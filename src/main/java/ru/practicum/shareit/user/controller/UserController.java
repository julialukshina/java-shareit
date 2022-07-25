package ru.practicum.shareit.user.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.service.UserService;

import javax.validation.Valid;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping(path = "/users")
@Validated
@Slf4j
public class UserController {
    private final UserService service;

    @Autowired
    public UserController(UserService service) {
        this.service = service;
    }

    @GetMapping //возвращает список пользователей
    public List<UserDto> getAllUsers() {
        return service.getAllUsers();
    }

    @PostMapping //создает нового пользователя
    public UserDto createNewUser(@Valid @RequestBody UserDto user) {
        return service.createNewUser(user);
    }

    @PatchMapping("/{userId}")//обновляет данные пользователя
    public UserDto updateUser(@PathVariable Long userId, @RequestBody Map<String, String> user) {
        return service.updateUser(userId, user);
    }

    @GetMapping("/{userId}") //возвращает пользователя по Id
    public UserDto getUserById(@PathVariable Long userId) {
        return service.getUserById(userId);
    }

    @DeleteMapping("/{userId}") //удаляет пользователя
    public void deleteUser(@PathVariable Long userId) {
        service.deleteUser(userId);
    }

}
