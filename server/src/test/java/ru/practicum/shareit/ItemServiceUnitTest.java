package ru.practicum.shareit;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.Mockito;
import ru.practicum.shareit.booking.repository.BookingRepository;
import ru.practicum.shareit.item.dto.CommentDto;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.mapper.ItemMapper;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.repository.CommentRepository;
import ru.practicum.shareit.item.repository.ItemRepository;
import ru.practicum.shareit.item.service.ItemServiceImpl;
import ru.practicum.shareit.request.repository.ItemRequestRepository;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserRepository;

import java.util.ArrayList;
import java.util.Optional;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;

public class ItemServiceUnitTest {
    @Mock
    private ItemRepository repository = Mockito.mock(ItemRepository.class);
    @Mock
    private UserRepository userRepository = Mockito.mock(UserRepository.class);
    @Mock
    private BookingRepository bookingRepository = Mockito.mock(BookingRepository.class);
    @Mock
    private CommentRepository commentRepository = Mockito.mock(CommentRepository.class);
    @Mock
    private ItemRequestRepository itemRequestRepository = Mockito.mock(ItemRequestRepository.class);
    private ItemServiceImpl itemService = new ItemServiceImpl(userRepository, repository, bookingRepository,
            commentRepository, itemRequestRepository);

    @Test
    public void createNewItem() {
        ItemMapper mapper = Mockito.mock(ItemMapper.class);
        itemService.setMapper(mapper);
        ItemDto itemDto = new ItemDto(1L, "Стол", "Журнальный стол", true, 1L, null,
                null, null, new ArrayList<CommentDto>());
        User user = new User(1L, "Tom", "Tom@gmail.com");
        Item item = new Item();
        item.setId(1L);
        item.setName("Стол");
        item.setDescription("Журнальный стол");
        item.setAvailable(true);
        item.setOwner(user);
        Mockito
                .when(userRepository.findById(anyLong()))
                .thenReturn(Optional.of(user));
        Mockito
                .when(repository.save(any(Item.class)))
                .thenReturn(item);
        Mockito
                .when(mapper.toItemDto(any()))
                .thenReturn(itemDto);
        Assertions.assertEquals(itemDto, itemService.createNewItem(1L, itemDto));
    }
}
