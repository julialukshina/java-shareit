package ru.practicum.shareit;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import ru.practicum.shareit.user.controller.UserController;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.service.UserServiceImpl;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
public class UserControllerTests {
    private UserDto user;
    private UserDto validUser = new UserDto(1, "sasha", "sashaivanova@yandex.ru");
    private String body;
    @Autowired
    ObjectMapper objectMapper;
    @Autowired
    private MockMvc mockMvc;
    @Autowired
    private UserController userController;
    @Autowired
    private UserServiceImpl service;

    @BeforeEach //перед каждым тестом в репозиторий добавляется пользователь
    public void createUserObject() throws Exception {
        user = new UserDto(1, "sasha", "sashaivanova@yandex.ru");
        service.clear();
        body = objectMapper.writeValueAsString(user);
        this.mockMvc.perform(post("/users").content(body).contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().json(objectMapper.writeValueAsString(validUser)));
        assertEquals(1, userController.getAllUsers().size());
    }

    @Test
    public void getAllUsersTest() throws Exception {
        this.mockMvc.perform(get("/users"))
                .andExpect(status().isOk());
    }

    @Test
    public void badCreateNewUserTest() throws Exception {
        user = new UserDto(1, "sasha", "sasha"); //некорректная почта
        body = objectMapper.writeValueAsString(user);
        badCreate(body);
        user = new UserDto(1, "sasha", ""); //пустая строка вместо почты
        body = objectMapper.writeValueAsString(user);
        badCreate(body);
        user = new UserDto(1, "sasha", null); //null вместо почты
        body = objectMapper.writeValueAsString(user);
        badCreate(body);
        user = new UserDto(1, "", "sashaivanova@yandex.ru");//пустая строка вместо имени
        badCreate(body);
        user = new UserDto(1, null, "sashaivanova@yandex.ru"); //null вместо имени
        badCreate(body);
    }

    @Test
    public void goodUpdateUserTest() throws Exception { //тест на корректное обновление объекта
        user.setName("Александра");
        body = "{\"name\": \"Александра\"}";
        goodUpdate(body);//метод PATCH будет выполнен корректно
        assertEquals(user.getName(), userController.getUserById(1L).getName());
        user.setEmail("sasha@yandex.com");
        body = "{\"email\": \"sasha@yandex.com\"}";
        goodUpdate(body);//метод PATCH будет выполнен корректно
        assertEquals(user.getEmail(), userController.getUserById(1L).getEmail());
    }

    @Test
    public void badUpdateUserTest() throws Exception { //тест на некорректное обновление объекта
        user.setEmail(""); //некорректное значение для email
        body = "{\"email\":\"\"}";
        badUpdate(body);
        assertEquals(validUser.getEmail(), userController.getUserById(1L).getEmail());
        user.setEmail("sashacom"); //некорректное значение для email
        body = "{\"email\": \"sashacom\"}";
        badUpdate(body);
        assertEquals(validUser.getEmail(), userController.getUserById(1L).getEmail());
        user.setName("");//некорректное значение для name
        body = "{\"name\":\"\"}";
        badUpdate(body);
        assertEquals(validUser.getName(), userController.getUserById(1L).getName());
    }

    @Test
    public void goodDeleteUserTest() throws Exception {//тест на корректное удаление объекта
        this.mockMvc.perform(delete("/users/1"))
                .andExpect(status().isOk());
        assertEquals(0, userController.getAllUsers().size());
    }

    @Test
    public void badDeleteUserTest() throws Exception { //тест на некорректное удаление объекта
        this.mockMvc.perform(delete("/users/100"))
                .andExpect(status().isNotFound());
        assertEquals(1, userController.getAllUsers().size());
    }

    @Test
    public void goodGetUserByIdTest() throws Exception { //тест на корректное получение объекта по id
        this.mockMvc.perform(get("/users/1"))
                .andExpect(status().isOk());
    }

    @Test
    public void bedGetUserByIdTest() throws Exception { //тест на некорректное получение объекта по id
        this.mockMvc.perform(get("/users/100"))
                .andExpect(status().isNotFound());
    }

    private void badCreate(String body) throws Exception { //метод некорректного создания объекта
        this.mockMvc.perform(post("/users").content(body).contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest());
        assertEquals(1, userController.getAllUsers().size());
    }

    private void goodUpdate(String body) throws Exception { //метод корректного обновления объекта
        this.mockMvc.perform(patch("/users/1").content(body).contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());
    }

    private void badUpdate(String body) throws Exception { //метод некорректного обновления объекта
        this.mockMvc.perform(patch("/users/1").content(body).contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest());
    }
}
