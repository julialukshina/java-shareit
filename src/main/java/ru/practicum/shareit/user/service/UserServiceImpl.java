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
import ru.practicum.shareit.user.model.User;
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
    public UserDto updateUser(Long userId, UserDto user) {
        userValidation(userId);
        if (user.getName() != null) {
            if (user.getName().isBlank()) {
                throw new UserValidationException("The name can't be empty");
            }
        }
        if (user.getEmail() != null) {
            if (user.getEmail().isBlank() || !EmailValidator.getInstance().isValid(user.getEmail())) {
                throw new UserValidationException("The name can't be empty");
            }
        }
        if (idEmails.containsValue(user.getEmail())) {
            throw new UserAlreadyExistException("A user with this email already exists");
        }
        repository.updateUser(userId, user.getName(), user.getEmail());
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

    @Override
    public User getUser(Long id) {
        return repository.getUserById(id);
    }

    //метод генерации id
    private long generateId() {
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
