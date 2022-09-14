package ru.practicum.shareit.users;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.OnCreate;
import ru.practicum.shareit.users.dto.UserGatewayDto;

import javax.validation.ConstraintViolationException;
import javax.validation.Valid;

@RestController
@RequestMapping(path = "/users")
@RequiredArgsConstructor
@Slf4j
@Validated
public class UserGatewayController {
    private final UserClient userClient;

    @GetMapping //возвращает список пользователей
    public ResponseEntity<Object> getAll() {
        return userClient.getAllUsers();
    }

    @Validated(OnCreate.class)
    @PostMapping //создает нового пользователя
    public ResponseEntity<Object> createUser(@Valid @RequestBody UserGatewayDto user) {
        return userClient.createNewUser(user);
    }

    @PatchMapping("/{userId}")//обновляет данные пользователя
    public ResponseEntity<Object> patchUser(@PathVariable Long userId, @RequestBody UserGatewayDto userGatewayDto) {
        return userClient.updateUser(userId, userGatewayDto);
    }

    @GetMapping("/{userId}") //возвращает пользователя по Id
    public ResponseEntity<Object> getById(@PathVariable Long userId) {
        return userClient.getUserById(userId);
    }

    @DeleteMapping("/{userId}") //удаляет пользователя
    public ResponseEntity<Object> deleteUserById(@PathVariable Long userId) {
        return userClient.deleteUser(userId);
    }

    @ExceptionHandler(ConstraintViolationException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ResponseEntity<String> handleConstraintViolationException(ConstraintViolationException e) {
        return new ResponseEntity<>("not valid due to validation error: " + e.getMessage(), HttpStatus.BAD_REQUEST);
    }
}
