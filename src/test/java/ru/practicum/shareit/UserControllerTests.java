package ru.practicum.shareit;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.user.controller.UserController;
import ru.practicum.shareit.user.dto.UserDto;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@AutoConfigureTestDatabase
public class UserControllerTests {
    private UserDto user;
    private final UserDto validUser = new UserDto(1, "sasha", "sashaivanova@yandex.ru");
    private String body;
    @Autowired
    ObjectMapper objectMapper;
    @Autowired
    private MockMvc mockMvc;
    @Autowired
    private UserController userController;
    @Autowired
    private JdbcTemplate jdbcTemplate;

    @BeforeEach //перед каждым тестом в репозиторий добавляется пользователь
    public void createUserObject() throws Exception {
        String sqlQuery = "ALTER TABLE requests ALTER COLUMN id RESTART WITH 1"; //скидываем счетчики
        jdbcTemplate.update(sqlQuery);
        sqlQuery = "ALTER TABLE comments ALTER COLUMN id RESTART WITH 1";
        jdbcTemplate.update(sqlQuery);
        sqlQuery = "ALTER TABLE bookings ALTER COLUMN id RESTART WITH 1";
        jdbcTemplate.update(sqlQuery);
        sqlQuery = "ALTER TABLE items ALTER COLUMN id RESTART WITH 1";
        jdbcTemplate.update(sqlQuery);
        sqlQuery = "ALTER TABLE users ALTER COLUMN id RESTART WITH 1";
        jdbcTemplate.update(sqlQuery);
        user = new UserDto(1, "sasha", "sashaivanova@yandex.ru");
        body = objectMapper.writeValueAsString(user);
        this.mockMvc.perform(post("/users").content(body).contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().json(objectMapper.writeValueAsString(validUser)));
        assertEquals(1, userController.getAllUsers().size());
    }

    @Test
    @Transactional
    public void getAllUsersTest() throws Exception {
        this.mockMvc.perform(get("/users"))
                .andExpect(status().isOk());
    }

    @Test
    @Transactional
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
    @Transactional
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
    @Transactional
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
    @Transactional//тест на корректное удаление объекта
    public void goodDeleteUserTest() throws Exception {
        this.mockMvc.perform(delete("/users/1"))
                .andExpect(status().isOk());
        assertEquals(0, userController.getAllUsers().size());
    }

    @Test
    @Transactional//тест на некорректное удаление объекта
    public void badDeleteUserTest() throws Exception {
        this.mockMvc.perform(delete("/users/100"))
                .andExpect(status().isNotFound());
        assertEquals(1, userController.getAllUsers().size());
    }

    @Test
    @Transactional//тест на корректное получение объекта по id
    public void goodGetUserByIdTest() throws Exception {
        this.mockMvc.perform(get("/users/1"))
                .andExpect(status().isOk());
    }

    @Test
    @Transactional
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
