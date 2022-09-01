package ru.practicum.shareit;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Lazy;
import org.springframework.http.MediaType;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.item.dto.CommentDto;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.requests.dto.ItemRequestDto;
import ru.practicum.shareit.requests.dto.ItemRequestShortDto;
import ru.practicum.shareit.requests.mapper.ItemRequestMapper;
import ru.practicum.shareit.requests.repository.ItemRequestRepository;
import ru.practicum.shareit.user.dto.UserDto;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import static org.hamcrest.Matchers.is;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@AutoConfigureTestDatabase
public class ItemRequestTests {
    @Autowired
    ObjectMapper objectMapper;
    @Autowired
    private MockMvc mockMvc;
    @Autowired
    private ItemRequestRepository repository;
    @Autowired
    private JdbcTemplate jdbcTemplate;
    private String body;
    @Lazy
    @Autowired
    private ItemRequestMapper mapper;
    private final UserDto user1 = new UserDto(1L, "sasha", "ivanova@yandex.ru");
    private final UserDto user2 = new UserDto(2L, "masha", "mashaivanova@yandex.ru");
    ;
    private final ItemDto item1 = new ItemDto(1L, "Стол", "Журнальный стол", true, 1L, null,
            null, null, new ArrayList<CommentDto>());
    private final ItemDto item2 = new ItemDto(2L, "Стул", "Офисный стул", true, 1L, null,
            null, null, new ArrayList<CommentDto>());
    LocalDateTime now = LocalDateTime.now();
    private final ItemRequestDto dto = new ItemRequestDto(1L, "дрель для смолы", 1L, now.plusMinutes(2),
            new ArrayList<ItemDto>());

    @BeforeEach //перед каждым тестом созается окружение
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
//        body = objectMapper.writeValueAsString(item1);
//        this.mockMvc.perform(post("/items").header("X-Sharer-User-Id", 1L)
//                .content(body).contentType(MediaType.APPLICATION_JSON)).andExpect(status().isOk());
//        body = objectMapper.writeValueAsString(item2);
//        this.mockMvc.perform(post("/items").header("X-Sharer-User-Id", 1L)
//                .content(body).contentType(MediaType.APPLICATION_JSON)).andExpect(status().isOk());
    }

    @Transactional
    @Test
    public void createItemRequest() throws Exception {
        ItemRequestShortDto shortDto = new ItemRequestShortDto("дрель для смолы");
        body = objectMapper.writeValueAsString(shortDto);
        this.mockMvc.perform(post("/requests").header("X-Sharer-User-Id", 1L)
                        .content(body).contentType(MediaType.APPLICATION_JSON)).andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(dto.getId()), Long.class))
                .andExpect(jsonPath("$.description", is(dto.getDescription())))
                .andExpect(jsonPath("$.requesterId", is(dto.getRequesterId()), Long.class));
        this.mockMvc.perform(post("/requests").header("X-Sharer-User-Id", 10L)
                .content(body).contentType(MediaType.APPLICATION_JSON)).andExpect(status().isNotFound());
        shortDto.setDescription("");
        body = objectMapper.writeValueAsString(shortDto);
        this.mockMvc.perform(post("/requests").header("X-Sharer-User-Id", 1L)
                .content(body).contentType(MediaType.APPLICATION_JSON)).andExpect(status().isBadRequest());
    }

    @Transactional
    @Test
    public void getItemRequestsOfUser() throws Exception {
        this.mockMvc.perform(get("/requests").header("X-Sharer-User-Id", 10L)
                .content(body).contentType(MediaType.APPLICATION_JSON)).andExpect(status().isNotFound());
        List<ItemRequestDto> requests = new ArrayList<>();
        this.mockMvc.perform(get("/requests").header("X-Sharer-User-Id", 1L)).andExpect(status().isOk())
                .andExpect(content().json(objectMapper.writeValueAsString(requests)));
        requests = createItemRequestsForTest();
        this.mockMvc.perform(get("/requests").header("X-Sharer-User-Id", 1L)).andExpect(status().isOk())
                .andExpect(content().json(objectMapper.writeValueAsString(requests)));
    }

    @Transactional
    @Test
    public void getAll() throws Exception {
        this.mockMvc.perform(get("/requests/all").header("X-Sharer-User-Id", 10L)
                .content(body).contentType(MediaType.APPLICATION_JSON)).andExpect(status().isNotFound());
        List<ItemRequestDto> requestsForUser1 = new ArrayList<>();
        List<ItemRequestDto> requestsForUser2 = createItemRequestsForTest();
        this.mockMvc.perform(get("/requests/all").header("X-Sharer-User-Id", 1L)).andExpect(status().isOk())
                .andExpect(content().json(objectMapper.writeValueAsString(requestsForUser1)));
        this.mockMvc.perform(get("/requests/all?from=0&size=2").header("X-Sharer-User-Id", 2L))
                .andExpect(status().isOk())
                .andExpect(content().json(objectMapper.writeValueAsString(requestsForUser2)));
        requestsForUser2.remove(0);
        this.mockMvc.perform(get("/requests/all?from=0&size=1").header("X-Sharer-User-Id", 2L))
                .andExpect(status().isOk())
                .andExpect(content().json(objectMapper.writeValueAsString(requestsForUser2)));

        this.mockMvc.perform(get("/requests/all?from=0&size=0").header("X-Sharer-User-Id", 2L))
                .andExpect(status().isBadRequest());
        this.mockMvc.perform(get("/requests/all?from=-1&size=1").header("X-Sharer-User-Id", 2L))
                .andExpect(status().isBadRequest());
        this.mockMvc.perform(get("/requests/all?from=0&size=-1").header("X-Sharer-User-Id", 2L))
                .andExpect(status().isBadRequest());
    }

    @Transactional
    @Test
    public void getById() throws Exception {
        ItemRequestShortDto shortDto = new ItemRequestShortDto("дрель для смолы");
        body = objectMapper.writeValueAsString(shortDto);
        this.mockMvc.perform(post("/requests").header("X-Sharer-User-Id", 1L)
                .content(body).contentType(MediaType.APPLICATION_JSON)).andExpect(status().isOk());
        this.mockMvc.perform(get("/requests/1").header("X-Sharer-User-Id", 1L)).andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(dto.getId()), Long.class))
                .andExpect(jsonPath("$.description", is(dto.getDescription())))
                .andExpect(jsonPath("$.requesterId", is(dto.getRequesterId()), Long.class));
        this.mockMvc.perform(get("/requests/1").header("X-Sharer-User-Id", 2L)).andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(dto.getId()), Long.class))
                .andExpect(jsonPath("$.description", is(dto.getDescription())))
                .andExpect(jsonPath("$.requesterId", is(dto.getRequesterId()), Long.class));
        this.mockMvc.perform(get("/requests/10").header("X-Sharer-User-Id", 1L))
                .andExpect(status().isNotFound());
        this.mockMvc.perform(get("/requests/1").header("X-Sharer-User-Id", 10L))
                .andExpect(status().isNotFound());
    }

    private List<ItemRequestDto> createItemRequestsForTest() throws Exception {
        List<ItemRequestDto> requests = new ArrayList<>();
        ItemRequestShortDto shortDto = new ItemRequestShortDto("дрель для смолы");
        body = objectMapper.writeValueAsString(shortDto);
        this.mockMvc.perform(post("/requests").header("X-Sharer-User-Id", 1L)
                .content(body).contentType(MediaType.APPLICATION_JSON)).andExpect(status().isOk());
        requests.add(mapper.toItemRequestDto(repository.findById(1L).get()));
        shortDto.setDescription("платье на выпускной");
        body = objectMapper.writeValueAsString(shortDto);
        this.mockMvc.perform(post("/requests").header("X-Sharer-User-Id", 1L)
                .content(body).contentType(MediaType.APPLICATION_JSON)).andExpect(status().isOk());
        requests.add(mapper.toItemRequestDto(repository.findById(2L).get()));
        return requests;
    }
}
