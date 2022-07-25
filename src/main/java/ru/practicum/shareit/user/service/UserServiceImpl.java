package ru.practicum.shareit.user.service;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.validator.routines.EmailValidator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.exception.users.UserAlreadyExistException;
import ru.practicum.shareit.exception.users.UserNotFoundException;
import ru.practicum.shareit.exception.users.UserValidationException;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.mapper.UserMapper;
import ru.practicum.shareit.user.repository.UserRepository;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@Slf4j
public class UserServiceImpl implements UserService {
    private final UserRepository repository;

    private final Map<Long, String> idEmails = new HashMap<>();
    private long id = 1;

    @Autowired
    public UserServiceImpl(UserRepository repository) {
        this.repository = repository;
    }

    @Override
    public List<UserDto> getAllUsers() {
        return new ArrayList<>(repository.getAllUsers().values()).stream()
                .map(UserMapper::toUserDto)
                .collect(Collectors.toList());
    }

    @Override
    public UserDto createNewUser(UserDto user) {
        if (idEmails.containsValue(user.getEmail())) {
            throw new UserAlreadyExistException("A user with this email already exists");
        }
        user.setId(generateId());
        repository.addUser(UserMapper.toUser(user));
        idEmails.put(user.getId(), user.getEmail());
        log.info("The new user has been created: {}", user);
        return user;
    }

    @Override
    public UserDto updateUser(Long userId, Map<String, String> user) {
        String name = null;
        String email = null;
        userValidation(userId);
        if (user.containsKey("name")) {
            if (!user.get("name").isBlank()) {
                name = user.get("name");
            } else {
                throw new UserValidationException("The name can't be empty");
            }

        }
        if (user.containsKey("email") && !EmailValidator.getInstance().isValid(user.get("email"))) {
            throw new UserValidationException("The email is incorrect");
        }
        if (user.containsKey("email") && EmailValidator.getInstance().isValid(user.get("email"))) {
            email = user.get("email");
            if (idEmails.containsValue(email)) {
                throw new UserAlreadyExistException("A user with this email already exists");
            }
        }
        repository.updateUser(userId, name, email);
        idEmails.put(userId, repository.getUserById(userId).getEmail());
        log.info("The user with id {} updated", userId);
        return getUserById(userId);
    }

    @Override
    public void deleteUser(Long id) {
        userValidation(id);
        repository.deleteUser(id);
        idEmails.remove(id);
        log.info("The user with id {} deleted", id);
    }

    @Override
    public UserDto getUserById(Long id) {
        userValidation(id);
        return UserMapper.toUserDto(repository.getUserById(id));
    }

    private long generateId() {//метод генерации id
        return id++;
    }

    private void userValidation(Long id) {
        if (!repository.getAllUsers().containsKey(id)) {
            throw new UserNotFoundException("This user is not found");
        }
    }

    public void clear() {
        repository.clear();
        idEmails.clear();
        id = 1;
    }

}
