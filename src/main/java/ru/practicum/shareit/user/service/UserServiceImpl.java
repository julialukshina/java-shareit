package ru.practicum.shareit.user.service;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.validator.routines.EmailValidator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.exception.users.UserNotFoundException;
import ru.practicum.shareit.exception.users.UserValidationException;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.mapper.UserMapper;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserRepository;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

@Service
@Slf4j
public class UserServiceImpl implements UserService {

    private final UserRepository repository;

    @Autowired
    public UserServiceImpl(UserRepository repository) {
        this.repository = repository;
    }

    @Override
    public List<UserDto> getAllUsers() {
        return new ArrayList<>((Collection) repository.findAll());
    }

    @Override
    public UserDto createNewUser(UserDto user) {
        UserDto dto = UserMapper.toUserDto(repository.save(UserMapper.toUser(user)));
        log.info("The new user has been created: {}", dto);
        return dto;
    }

    @Override
    public UserDto updateUser(Long userId, UserDto user) {
        userValidation(userId);
        User updateUser = UserMapper.toUser(getUserById(userId));
        if (user.getName() != null) {
            if (user.getName().isBlank()) {
                throw new UserValidationException("The name can't be empty");
            }
            updateUser.setName(user.getName());
        }
        if (user.getEmail() != null) {
            if (user.getEmail().isBlank() || !EmailValidator.getInstance().isValid(user.getEmail())) {
                throw new UserValidationException("The name can't be empty");
            }
            updateUser.setEmail(user.getEmail());
        }
        user.setId(userId);
        repository.save(updateUser);
        log.info("The user with id {} updated", userId);
        return getUserById(userId);
    }

    @Override
    public void deleteUser(Long id) {
        userValidation(id);
        repository.deleteById(id);
        log.info("The user with id {} deleted", id);
    }

    @Override
    public UserDto getUserById(Long id) {
        userValidation(id);
        return UserMapper.toUserDto(repository.findById(id).get());
    }

    @Override
    public User getUser(Long id) {
        return repository.findById(id).get();
    }

    private void userValidation(Long id) {
        if (repository.findById(id).isEmpty()) {
            throw new UserNotFoundException("This user is not found");
        }
    }
}
