package ru.practicum.shareit;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.api.TestInstance.Lifecycle;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.booking.repository.BookingRepository;
import ru.practicum.shareit.item.controller.ItemController;
import ru.practicum.shareit.item.dto.CommentDto;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.repository.CommentRepository;
import ru.practicum.shareit.item.service.ItemServiceImpl;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.service.UserServiceImpl;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@AutoConfigureTestDatabase
@TestInstance(Lifecycle.PER_CLASS)
public class ItemControllerTest {
    private final UserDto user1 = new UserDto(1, "sasha", "ivanova@yandex.ru");
    private final UserDto user2 = new UserDto(2, "masha", "mashaivanova@yandex.ru");

    private final ItemDto validItem = new ItemDto(1L, "Стол", "Журнальный стол", true, 1, null,
            null, new ArrayList<CommentDto>());

    private ItemDto item1;
    private String body;
    @Autowired
    ObjectMapper objectMapper;
    @Autowired
    private MockMvc mockMvc;
    @Autowired
    private ItemController controller;
    @Autowired
    private ItemServiceImpl service;
    @Autowired
    private UserServiceImpl userService;
    @Autowired
    private BookingRepository bookingRepository;
    @Autowired
    private CommentRepository commentRepository;
    @Autowired
    private JdbcTemplate jdbcTemplate;

    @BeforeEach //перед каждым тестом в репозиторий добавляется вещь
    public void createItemObject() throws Exception {
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
        body = objectMapper.writeValueAsString(user1);
        this.mockMvc.perform(post("/users").content(body).contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());
        body = objectMapper.writeValueAsString(user2);
        this.mockMvc.perform(post("/users").content(body).contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());
        item1 = new ItemDto(1L, "Стол", "Журнальный стол", true, 1, null,
                null, new ArrayList<CommentDto>());
        body = objectMapper.writeValueAsString(item1);
        goodCreate(body);
        assertEquals(validItem, controller.getItemById(1L, 1L));
    }

    @Transactional
    @Test
    public void badCreateNewItem() throws Exception {
        //некорректный собственник
        item1 = new ItemDto(2L, "Стол", "Журнальный стол", true, 100, null,
                null, new ArrayList<CommentDto>());
        body = objectMapper.writeValueAsString(item1);
        this.mockMvc.perform(post("/items").header("X-Sharer-User-Id", 100).content(body)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound());
        assertEquals(1, service.getAllItems().size());
        //некорректное имя
        item1 = new ItemDto(2L, "", "Журнальный стол", true, 1, null,
                null, new ArrayList<CommentDto>());
        body = objectMapper.writeValueAsString(item1);
        badCreate(body);
        //некорректное описание
        item1 = new ItemDto(2L, "Стол", "", true, 1, null,
                null, new ArrayList<CommentDto>());
        body = objectMapper.writeValueAsString(item1);
        badCreate(body);
        //некорректный статус
        item1 = new ItemDto(2L, "Стол", "Журнальный стол", true, 1, null,
                null, new ArrayList<CommentDto>());
        item1.setAvailable(null);
        body = objectMapper.writeValueAsString(item1);
        badCreate(body);
    }

    @Transactional
    @Test
    public void goodUpdateTest() throws Exception {
        item1 = new ItemDto(2L, "Стол", "Журнальный стол", true, 1, null,
                null, new ArrayList<CommentDto>());
        body = objectMapper.writeValueAsString(item1);
        goodCreate(body);
        item1.setName("Столик");
        body = objectMapper.writeValueAsString(item1);
        goodUpdate(body);
        assertEquals(item1.getName(), service.getItemById(1L, 2L).getName());
        item1.setDescription("Небольшой журнальный столик");
        body = objectMapper.writeValueAsString(item1);
        goodUpdate(body);
        assertEquals(item1.getDescription(), service.getItemById(1L, 2L).getDescription());
        item1.setAvailable(false);
        body = objectMapper.writeValueAsString(item1);
        goodUpdate(body);
        assertEquals(item1.getAvailable(), service.getItemById(1L, 2L).getAvailable());
    }

    @Transactional
    @Test
    public void badUpdateTest() throws Exception {
        item1 = new ItemDto(2L, "Стол", "Журнальный стол", true, 100, null,
                null, new ArrayList<CommentDto>());
        body = objectMapper.writeValueAsString(item1);
        this.mockMvc.perform(patch("/items/2").header("X-Sharer-User-Id", 1).content(body)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound());
        item1.setOwnerId(1);
        body = objectMapper.writeValueAsString(item1);
        goodCreate(body);
        item1.setName("");
        body = objectMapper.writeValueAsString(item1);
        badUpdate(body);
        item1.setName("Стол");
        item1.setDescription("");
        body = objectMapper.writeValueAsString(item1);
        badUpdate(body);
    }

    @Transactional
    @Test
    public void goodGetItemByIdTest() throws Exception {
        this.mockMvc.perform(get("/items/1").header("X-Sharer-User-Id", 1))
                .andExpect(status().isOk());
    }

    @Transactional
    @Test
    public void badGetItemByIdTest() throws Exception {
        this.mockMvc.perform(get("/items/100").header("X-Sharer-User-Id", 1))
                .andExpect(status().isNotFound());
    }


    @Transactional
    @Test
    public void goodDeleteItemTest() throws Exception {
        this.mockMvc.perform(delete("/items/1").header("X-Sharer-User-Id", 1))
                .andExpect(status().isOk());
    }

    @Transactional
    @Test
    public void badDeleteItemTest() throws Exception {
        this.mockMvc.perform(delete("/items/1").header("X-Sharer-User-Id", 100))
                .andExpect(status().isNotFound());
        this.mockMvc.perform(delete("/items/100").header("X-Sharer-User-Id", 1))
                .andExpect(status().isNotFound());
    }

    @Transactional
    @Test
    public void goodSearchTest() throws Exception {
        List<ItemDto> items = new ArrayList<>();
        this.mockMvc.perform(get("/items/search?text=дом"))
                .andExpect(status().isOk())
                .andExpect(content().json(objectMapper.writeValueAsString(items)));
        item1 = new ItemDto(1L, "Стол", "Журнальный стол", true, 1, null,
                null, new ArrayList<CommentDto>());
        items.add(item1);
        item1 = new ItemDto(2L, "Стул", "Обеденнный стол", true, 1, null,
                null, new ArrayList<CommentDto>());
        items.add(item1);
        body = objectMapper.writeValueAsString(item1);
        goodCreate(body);
        this.mockMvc.perform(get("/items/search?text=стОл"))
                .andExpect(status().isOk())
                .andExpect(content().json(objectMapper.writeValueAsString(items)));
        item1.setAvailable(false);
        body = objectMapper.writeValueAsString(item1);
        goodUpdate(body);
        items.remove(1);
        this.mockMvc.perform(get("/items/search?text=стОл"))
                .andExpect(status().isOk())
                .andExpect(content().json(objectMapper.writeValueAsString(items)));
    }

    @Transactional
    @Test
    public void goodGetItemsOfUserTest() throws Exception {
        List<ItemDto> items = new ArrayList<>();
        this.mockMvc.perform(get("/items").header("X-Sharer-User-Id", 2))
                .andExpect(status().isOk())
                .andExpect(content().json(objectMapper.writeValueAsString(items)));
        item1 = new ItemDto(1L, "Стол", "Журнальный стол", true, 1, null,
                null, new ArrayList<CommentDto>());
        items.add(item1);
        this.mockMvc.perform(get("/items").header("X-Sharer-User-Id", 1))
                .andExpect(status().isOk())
                .andExpect(content().json(objectMapper.writeValueAsString(items)));
    }

    private void goodCreate(String body) throws Exception {
        this.mockMvc.perform(post("/items").header("X-Sharer-User-Id", 1)
                        .content(body).contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());
    }

    private void badCreate(String body) throws Exception { //метод некорректного создания объекта
        this.mockMvc.perform(post("/items").header("X-Sharer-User-Id", 1)
                        .content(body).contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest());
        assertEquals(1, service.getAllItems().size());
    }

    private void goodUpdate(String body) throws Exception {
        this.mockMvc.perform(patch("/items/2").header("X-Sharer-User-Id", 1)
                        .content(body).contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());
    }

    private void badUpdate(String body) throws Exception {
        this.mockMvc.perform(patch("/items/2").header("X-Sharer-User-Id", 1)
                        .content(body).contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest());
    }
}
