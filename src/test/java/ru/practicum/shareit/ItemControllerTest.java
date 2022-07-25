package ru.practicum.shareit;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.api.TestInstance.Lifecycle;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import ru.practicum.shareit.item.controller.ItemController;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.service.ItemServiceImpl;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.service.UserService;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@TestInstance(Lifecycle.PER_CLASS)
public class ItemControllerTest {
    private UserDto user1 = new UserDto(1, "sasha", "ivanova@yandex.ru");
    private UserDto user2 = new UserDto(2, "masha", "mashaivanova@yandex.ru");

    private ItemDto validItem = new ItemDto(1L, "Стол", "Журнальный стол", true, 1, null);

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
    private UserService userService;

    @BeforeAll //перед всеми тестами в репозиторий пользователей добавляется два пользователя
    public void createUserObject() throws Exception {
        userService.createNewUser(user1);
        userService.createNewUser(user2);
    }

    @BeforeEach //перед каздым тестом в репозиторий добавляется вещь
    public void createItemObject() throws Exception { //
        service.clear();
        item1 = new ItemDto(1L, "Стол", "Журнальный стол", true, 1, null);
        body = objectMapper.writeValueAsString(item1);
        goodCreate(body);
        assertEquals(validItem, controller.getItemById(1L));
    }

    @Test
    public void badCreateNewItem() throws Exception {
        //некорректный собственник
        item1 = new ItemDto(2L, "Стол", "Журнальный стол", true, 100, null);
        body = objectMapper.writeValueAsString(item1);
        this.mockMvc.perform(post("/items").header("X-Sharer-User-Id", 100).content(body).contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound());
        assertEquals(1, service.getAllItems().size());
        //некорректное имя
        item1 = new ItemDto(2L, "", "Журнальный стол", true, 1, null);
        body = objectMapper.writeValueAsString(item1);
        badCreate(body);
        //некорректное описание
        item1 = new ItemDto(2L, "Стол", "", true, 1, null);
        body = objectMapper.writeValueAsString(item1);
        badCreate(body);
        //некорректный статус
        item1 = new ItemDto(2L, "Стол", "Журнальный стол", true, 1, null);
        item1.setAvailable(null);
        body = objectMapper.writeValueAsString(item1);
        badCreate(body);
    }

    @Test
    public void goodUpdateTest() throws Exception {
        item1 = new ItemDto(2L, "Стол", "Журнальный стол", true, 1, null);
        body = objectMapper.writeValueAsString(item1);
        goodCreate(body);
        item1.setName("Столик");
        body = objectMapper.writeValueAsString(item1);
        goodUpdate(body);
        assertEquals(item1.getName(), service.getItemById(2L).getName());
        item1.setDescription("Небольшой журнальный столик");
        body = objectMapper.writeValueAsString(item1);
        goodUpdate(body);
        assertEquals(item1.getDescription(), service.getItemById(2L).getDescription());
        item1.setAvailable(false);
        body = objectMapper.writeValueAsString(item1);
        goodUpdate(body);
        assertEquals(item1.getAvailable(), service.getItemById(2L).getAvailable());
    }

    @Test
    public void badUpdateTest() throws Exception {
        item1 = new ItemDto(2L, "Стол", "Журнальный стол", true, 100, null);
        body = objectMapper.writeValueAsString(item1);
        this.mockMvc.perform(patch("/items/2").header("X-Sharer-User-Id", 1).content(body).contentType(MediaType.APPLICATION_JSON))
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

    @Test
    public void goodGetItemByIdTest() throws Exception {
        this.mockMvc.perform(get("/items/1")).
                andExpect(status().isOk());
    }

    @Test
    public void badGetItemByIdTest() throws Exception {
        this.mockMvc.perform(get("/items/100")).
                andExpect(status().isNotFound());
    }


    @Test
    public void goodDeleteItemTest() throws Exception {
        this.mockMvc.perform(delete("/items/1").header("X-Sharer-User-Id", 1)).
                andExpect(status().isOk());
    }

    @Test
    public void badDeleteItemTest() throws Exception {
        this.mockMvc.perform(delete("/items/1").header("X-Sharer-User-Id", 100)).
                andExpect(status().isNotFound());
        this.mockMvc.perform(delete("/items/100").header("X-Sharer-User-Id", 1)).
                andExpect(status().isNotFound());
    }

    @Test
    public void goodSearchTest() throws Exception {
        List<ItemDto> items = new ArrayList<>();
        this.mockMvc.perform(get("/items/search?text=дом"))
                .andExpect(status().isOk())
                .andExpect(content().json(objectMapper.writeValueAsString(items)));
        item1 = new ItemDto(1L, "Стол", "Журнальный стол", true, 1, null);
        items.add(item1);
        item1 = new ItemDto(2L, "Стул", "Обеденнный стол", true, 1, null);
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

    @Test
    public void goodGetItemsOfUserTest() throws Exception {
        List<ItemDto> items = new ArrayList<>();
        this.mockMvc.perform(get("/items").header("X-Sharer-User-Id", 2))
                .andExpect(status().isOk())
                .andExpect(content().json(objectMapper.writeValueAsString(items)));
        item1 = new ItemDto(1L, "Стол", "Журнальный стол", true, 1, null);
        items.add(item1);
        this.mockMvc.perform(get("/items").header("X-Sharer-User-Id", 1))
                .andExpect(status().isOk())
                .andExpect(content().json(objectMapper.writeValueAsString(items)));
    }

    private void goodCreate(String body) throws Exception {
        this.mockMvc.perform(post("/items").header("X-Sharer-User-Id", 1).content(body).contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());
    }

    private void badCreate(String body) throws Exception { //метод некорректного создания объекта
        this.mockMvc.perform(post("/items").header("X-Sharer-User-Id", 1).content(body).contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest());
        assertEquals(1, service.getAllItems().size());
    }

    private void goodUpdate(String body) throws Exception {
        this.mockMvc.perform(patch("/items/2").header("X-Sharer-User-Id", 1).content(body).contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());
    }

    private void badUpdate(String body) throws Exception {
        this.mockMvc.perform(patch("/items/2").header("X-Sharer-User-Id", 1).content(body).contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest());
    }
}
