package ru.practicum.shareit.user.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.OnCreate;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.service.UserService;

import javax.validation.ConstraintViolationException;
import javax.validation.Valid;
import java.util.List;

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

    @Validated(OnCreate.class)
    @PostMapping //создает нового пользователя
    public UserDto createNewUser(@Valid @RequestBody UserDto user) {
        return service.createNewUser(user);
    }

    @PatchMapping("/{userId}")//обновляет данные пользователя
    public UserDto updateUser(@PathVariable Long userId, @RequestBody UserDto userDto) {
        return service.updateUser(userId, userDto);
    }

    @GetMapping("/{userId}") //возвращает пользователя по Id
    public UserDto getUserById(@PathVariable Long userId) {
        return service.getUserById(userId);
    }

    @DeleteMapping("/{userId}") //удаляет пользователя
    public UserDto deleteUser(@PathVariable Long userId) {
        return service.deleteUser(userId);
    }

    @ExceptionHandler(ConstraintViolationException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ResponseEntity<String> handleConstraintViolationException(ConstraintViolationException e) {
        return new ResponseEntity<>("not valid due to validation error: " + e.getMessage(), HttpStatus.BAD_REQUEST);
    }

}
