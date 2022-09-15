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
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.dto.BookingShortDto;
import ru.practicum.shareit.booking.mapper.BookingMapper;
import ru.practicum.shareit.booking.model.BookingStatus;
import ru.practicum.shareit.booking.repository.BookingRepository;
import ru.practicum.shareit.item.dto.CommentDto;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.user.dto.UserDto;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import static org.hamcrest.Matchers.is;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@AutoConfigureTestDatabase
public class BookingControllerTests {
    @Autowired
    ObjectMapper objectMapper;
    @Autowired
    private MockMvc mockMvc;
    @Autowired
    private BookingRepository repository;
    @Autowired
    private JdbcTemplate jdbcTemplate;
    @Lazy
    @Autowired
    private BookingMapper mapper;
    private String body;
    private final UserDto user1 = new UserDto(1L, "sasha", "ivanova@yandex.ru");
    private UserDto user2 = new UserDto(2L, "masha", "mashaivanova@yandex.ru");
    private final ItemDto item1 = new ItemDto(1L, "Стол", "Журнальный стол", true, 1L, null,
            null, null, new ArrayList<CommentDto>());
    private ItemDto item2 = new ItemDto(2L, "Стул", "Офисный стул", true, 1L, null,
            null, null, new ArrayList<CommentDto>());
    private BookingShortDto shortDto;
    private LocalDateTime now = LocalDateTime.now();
    private BookingDto dto = new BookingDto(1L, now.plusMinutes(2), now.plusMinutes(4), item1, user2, BookingStatus.WAITING);

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
        body = objectMapper.writeValueAsString(item1);
        this.mockMvc.perform(post("/items").header("X-Sharer-User-Id", 1L)
                .content(body).contentType(MediaType.APPLICATION_JSON)).andExpect(status().isOk());
        body = objectMapper.writeValueAsString(item2);
        this.mockMvc.perform(post("/items").header("X-Sharer-User-Id", 1L)
                .content(body).contentType(MediaType.APPLICATION_JSON)).andExpect(status().isOk());
        shortDto = new BookingShortDto(now.plusMinutes(2), now.plusMinutes(4), 1L);
        body = objectMapper.writeValueAsString(shortDto);
        goodCreate(body, 2L);
    }

    @Transactional
    @Test
    public void badCreate() throws Exception {
        //несуществующая вещь
        shortDto = new BookingShortDto(now.plusMinutes(2), now.plusMinutes(4), 5L);
        body = objectMapper.writeValueAsString(shortDto);
        notFoundCreate(body, 1L);
        //несуществующий пользователь
        shortDto = new BookingShortDto(now.plusMinutes(2), now.plusMinutes(4), 1L);
        body = objectMapper.writeValueAsString(shortDto);
        notFoundCreate(body, 10L);
        //бронирование собственником
        shortDto = new BookingShortDto(now.plusMinutes(2), now.plusMinutes(4), 1L);
        body = objectMapper.writeValueAsString(shortDto);
        notFoundCreate(body, 1L);
        //окончание букинга раньше его старта
        shortDto = new BookingShortDto(now.plusMinutes(2), now.plusMinutes(1), 2L);
        body = objectMapper.writeValueAsString(shortDto);
        badRequestCreate(body, 2L);
        //бронирование недоступной к бронированию вещи
        item2.setAvailable(false);
        body = objectMapper.writeValueAsString(item2);
        this.mockMvc.perform(patch("/items/2").header("X-Sharer-User-Id", 1L)
                        .content(body).contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());
        shortDto = new BookingShortDto(now.plusMinutes(2), now.plusMinutes(4), 2L);
        body = objectMapper.writeValueAsString(shortDto);
        badRequestCreate(body, 2L);
    }

    @Transactional
    @Test
    public void update() throws Exception {
        //не передан параметр approved
        this.mockMvc.perform(patch("/bookings/1").header("X-Sharer-User-Id", 1L))
                .andExpect(status().isBadRequest());
        //несуществующий букинг
        this.mockMvc.perform(patch("/bookings/5?approved=true").header("X-Sharer-User-Id", 1L))
                .andExpect(status().isNotFound());
        //несуществующий пользователь
        this.mockMvc.perform(patch("/bookings/1?approved=true").header("X-Sharer-User-Id", 10L))
                .andExpect(status().isNotFound());
        //подтверждение бронирование несобственником
        this.mockMvc.perform(patch("/bookings/1?approved=true").header("X-Sharer-User-Id", 2L))
                .andExpect(status().isNotFound());
        //успешное подтверждение букинга
        this.mockMvc.perform(patch("/bookings/1?approved=true").header("X-Sharer-User-Id", 1L))
                .andExpect(status().isOk());
        //подтверждение уже подтвержденного букинга
        this.mockMvc.perform(patch("/bookings/1?approved=true").header("X-Sharer-User-Id", 1L))
                .andExpect(status().isBadRequest());
    }

    @Transactional
    @Test
    public void getBookingById() throws Exception {
        //запрос от собственника вещи
        this.mockMvc.perform(get("/bookings/1").header("X-Sharer-User-Id", 1L))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(dto.getId()), Long.class));
        //запрос от создателя букинга
        this.mockMvc.perform(get("/bookings/1").header("X-Sharer-User-Id", 2L))
                .andExpect(status().isOk());
        //запрос от несуществующего пользователя
        this.mockMvc.perform(get("/bookings/1").header("X-Sharer-User-Id", 20L))
                .andExpect(status().isNotFound());
        //запрос несуществующего букинга
        this.mockMvc.perform(get("/bookings/10").header("X-Sharer-User-Id", 2L))
                .andExpect(status().isNotFound());
        //запрос от пользователя, не являющегося собственником или создателем букинга
        user2 = new UserDto(3L, "sveta", "sveta@yandex.ru");
        body = objectMapper.writeValueAsString(user2);
        this.mockMvc.perform(post("/users").content(body).contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());
        this.mockMvc.perform(get("/bookings/1").header("X-Sharer-User-Id", 3L))
                .andExpect(status().isNotFound());
    }

    @Test
    @Transactional
    public void getAllForBooker() throws Exception {
        List<BookingDto> bookings = new ArrayList<>();
        //пустой лист для пользователя, не создававшего букинги
        this.mockMvc.perform(get("/bookings?state=ALL&from=0&size=1").header("X-Sharer-User-Id", 1L))
                .andExpect(status().isOk())
                .andExpect(content().json(objectMapper.writeValueAsString(bookings)));
        //пустой лист, так как нет букингов, отвечающих условию
        this.mockMvc.perform(get("/bookings?state=PAST&from=0&size=1").header("X-Sharer-User-Id", 2L))
                .andExpect(status().isOk())
                .andExpect(content().json(objectMapper.writeValueAsString(bookings)));
        bookings.add(dto);
        // корректное возвращение списка букингов с корректными параметрами
        this.mockMvc.perform(get("/bookings?state=ALL&from=0&size=1").header("X-Sharer-User-Id", 2L))
                .andExpect(status().isOk())
                .andExpect(content().json(objectMapper.writeValueAsString(bookings)));
        // корректное возвращение списка букингов с корректными параметрами и state=WAITING
        this.mockMvc.perform(get("/bookings?state=WAITING&from=0&size=1").header("X-Sharer-User-Id", 2L))
                .andExpect(status().isOk())
                .andExpect(content().json(objectMapper.writeValueAsString(bookings)));

        //корректная работа с различными значениями state
        BookingDto booking2 = new BookingDto(2L, now.minusMinutes(2), now.plusMinutes(4), item2, user2, BookingStatus.REJECTED);
        bookings.add(booking2);
        repository.save(mapper.toBooking(booking2));
        this.mockMvc.perform(get("/bookings?state=ALL&from=0&size=2").header("X-Sharer-User-Id", 2L))
                .andExpect(status().isOk())
                .andExpect(content().json(objectMapper.writeValueAsString(bookings)));
        bookings.remove(0);
        this.mockMvc.perform(get("/bookings?state=CURRENT&from=0&size=1").header("X-Sharer-User-Id", 2L))
                .andExpect(status().isOk())
                .andExpect(content().json(objectMapper.writeValueAsString(bookings)));
        booking2.setEnd(now.minusMinutes(1));
        bookings.set(0, booking2);
        repository.save(mapper.toBooking(booking2));
        this.mockMvc.perform(get("/bookings?state=REJECTED&from=0&size=1").header("X-Sharer-User-Id", 2L))
                .andExpect(status().isOk())
                .andExpect(content().json(objectMapper.writeValueAsString(bookings)));
        this.mockMvc.perform(get("/bookings?state=PAST&from=0&size=1").header("X-Sharer-User-Id", 2L))
                .andExpect(status().isOk())
                .andExpect(content().json(objectMapper.writeValueAsString(bookings)));
        booking2.setStart(now.plusMinutes(5));
        booking2.setEnd(now.plusMinutes(10));
        bookings.set(0, booking2);
        repository.save(mapper.toBooking(booking2));
        this.mockMvc.perform(get("/bookings?state=FUTURE&from=0&size=1").header("X-Sharer-User-Id", 2L))
                .andExpect(status().isOk())
                .andExpect(content().json(objectMapper.writeValueAsString(bookings)));
    }

    @Test
    @Transactional
    public void getAllForOwner() throws Exception {
        List<BookingDto> bookings = new ArrayList<>();
        //исключение для пользователя, не владеющего вещами, по которым были букинги
        this.mockMvc.perform(get("/bookings/owner?state=ALL&from=0&size=1").header("X-Sharer-User-Id", 2L))
                .andExpect(status().isNotFound());
        //пустой лист, так как нет букингов, отвечающих условию
        this.mockMvc.perform(get("/bookings/owner?state=PAST&from=0&size=1").header("X-Sharer-User-Id", 1L))
                .andExpect(status().isOk())
                .andExpect(content().json(objectMapper.writeValueAsString(bookings)));
        bookings.add(dto);
        // корректное возвращение списка букингов с корректными параметрами
        this.mockMvc.perform(get("/bookings/owner?state=ALL&from=0&size=1").header("X-Sharer-User-Id", 1L))
                .andExpect(status().isOk())
                .andExpect(content().json(objectMapper.writeValueAsString(bookings)));
        // корректное возвращение списка букингов с корректными параметрами и state=WAITING
        this.mockMvc.perform(get("/bookings/owner?state=WAITING&from=0&size=1").header("X-Sharer-User-Id", 1L))
                .andExpect(status().isOk())
                .andExpect(content().json(objectMapper.writeValueAsString(bookings)));

        //корректная работа с различными значениями state
        BookingDto booking2 = new BookingDto(2L, now.minusMinutes(2), now.plusMinutes(4), item2, user2, BookingStatus.REJECTED);
        bookings.add(booking2);
        repository.save(mapper.toBooking(booking2));
        this.mockMvc.perform(get("/bookings/owner?state=ALL&from=0&size=2").header("X-Sharer-User-Id", 1L))
                .andExpect(status().isOk())
                .andExpect(content().json(objectMapper.writeValueAsString(bookings)));
        bookings.remove(0);
        this.mockMvc.perform(get("/bookings/owner?state=CURRENT&from=0&size=1").header("X-Sharer-User-Id", 1L))
                .andExpect(status().isOk())
                .andExpect(content().json(objectMapper.writeValueAsString(bookings)));
        booking2.setEnd(now.minusMinutes(1));
        bookings.set(0, booking2);
        repository.save(mapper.toBooking(booking2));
        this.mockMvc.perform(get("/bookings/owner?state=REJECTED&from=0&size=1").header("X-Sharer-User-Id", 1L))
                .andExpect(status().isOk())
                .andExpect(content().json(objectMapper.writeValueAsString(bookings)));
        this.mockMvc.perform(get("/bookings/owner?state=PAST&from=0&size=1").header("X-Sharer-User-Id", 1L))
                .andExpect(status().isOk())
                .andExpect(content().json(objectMapper.writeValueAsString(bookings)));
        booking2.setStart(now.plusMinutes(5));
        booking2.setEnd(now.plusMinutes(10));
        bookings.set(0, booking2);
        repository.save(mapper.toBooking(booking2));
        this.mockMvc.perform(get("/bookings/owner?state=FUTURE&from=0&size=1").header("X-Sharer-User-Id", 1L))
                .andExpect(status().isOk())
                .andExpect(content().json(objectMapper.writeValueAsString(bookings)));
    }

    private void goodCreate(String body, Long userId) throws Exception {
        this.mockMvc.perform(post("/bookings").header("X-Sharer-User-Id", userId)
                        .content(body).contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());
    }

    private void badRequestCreate(String body, Long userId) throws Exception { //метод некорректного создания объекта
        this.mockMvc.perform(post("/bookings").header("X-Sharer-User-Id", userId)
                        .content(body).contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest());
    }

    private void notFoundCreate(String body, Long userId) throws Exception { //метод некорректного создания объекта
        this.mockMvc.perform(post("/bookings").header("X-Sharer-User-Id", userId)
                        .content(body).contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound());
    }
}
