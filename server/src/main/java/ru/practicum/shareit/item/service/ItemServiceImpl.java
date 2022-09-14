package ru.practicum.shareit.item.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.MyPageable;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.mapper.BookingForItemDtoMapper;
import ru.practicum.shareit.booking.mapper.BookingMapper;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.repository.BookingRepository;
import ru.practicum.shareit.exception.bookings.BookingValidateException;
import ru.practicum.shareit.exception.itemRequests.ItemRequestNotFoundException;
import ru.practicum.shareit.exception.items.ItemNotFoundException;
import ru.practicum.shareit.exception.items.ItemValidationException;
import ru.practicum.shareit.exception.items.NotOwnerException;
import ru.practicum.shareit.exception.users.UserNotFoundException;
import ru.practicum.shareit.item.dto.CommentDto;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.mapper.CommentMapper;
import ru.practicum.shareit.item.mapper.ItemMapper;
import ru.practicum.shareit.item.model.Comment;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.repository.CommentRepository;
import ru.practicum.shareit.item.repository.ItemRepository;
import ru.practicum.shareit.request.repository.ItemRequestRepository;
import ru.practicum.shareit.user.repository.UserRepository;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
@Slf4j
public class ItemServiceImpl implements ItemService {
    private final UserRepository userRepository;
    private final ItemRepository repository;
    private final BookingRepository bookingRepository;
    private final CommentRepository commentRepository;
    private final ItemRequestRepository itemRequestRepository;

    @Lazy
    @Autowired
    private ItemMapper mapper;

    @Lazy
    @Autowired
    private BookingMapper bookingMapper;
    @Lazy
    @Autowired
    private BookingForItemDtoMapper bookingForItemDtoMapper;

    @Autowired
    public ItemServiceImpl(UserRepository userRepository, ItemRepository repository, BookingRepository bookingRepository,
                           CommentRepository commentRepository, ItemRequestRepository itemRequestRepository) {
        this.userRepository = userRepository;
        this.repository = repository;
        this.bookingRepository = bookingRepository;
        this.commentRepository = commentRepository;
        this.itemRequestRepository = itemRequestRepository;
    }

    @Override
    public List<ItemDto> getAllItems() {
        return repository.findAll().stream()
                .map(mapper::toItemDto)
                .collect(Collectors.toList());
    }

    @Override
    public ItemDto createNewItem(long userId, ItemDto itemDto) {
        userValidation(userId);
        if (itemDto.getRequestId() != null) {
            itemRequestValidation(itemDto.getRequestId());
        }
        itemDto.setOwnerId(userId);
        ItemDto dto = mapper.toItemDto(repository.save(mapper.toItem(itemDto)));
        log.info("The new item has been created: {}", dto);
        return dto;
    }

    @Override
    public ItemDto updateItem(long userId, Long itemId, ItemDto itemDto) {
        userValidation(userId);
        itemValidation(itemId);
        Item updateItem = mapper.toItem(getItemById(userId, itemId));
        if (userId != repository.findById(itemId).get().getOwner().getId()) {
            throw new NotOwnerException("The item can be updated only by the owner");
        }
        if (itemDto.getName() != null) {
            if (itemDto.getName().isBlank()) {
                throw new ItemValidationException("The name can not be empty");
            }
            updateItem.setName(itemDto.getName());
        }
        if (itemDto.getDescription() != null) {
            if (itemDto.getDescription().isBlank()) {
                throw new ItemValidationException("The name can not be empty");
            }
            updateItem.setDescription(itemDto.getDescription());
        }
        if (itemDto.getAvailable() != null) {
            updateItem.setAvailable(itemDto.getAvailable());
        }
        repository.save(updateItem);
        log.info("The item with id {} updated", itemId);
        return getItemById(userId, itemId);
    }


    @Override
    public ItemDto getItemById(long userId, Long itemId) {
        itemValidation(itemId);
        userValidation(userId);
        ItemDto itemDto = mapper.toItemDto(repository.findById(itemId).get());
        fillCommentsToItemDto(itemDto);
        if (itemDto.getOwnerId() == userId) {
            return fillBookingInItemDto(itemDto);
        }
        return itemDto;
    }

    @Override
    public void deleteItem(Long userId, Long itemId) {
        userValidation(userId);
        itemValidation(itemId);
        if (userId != repository.findById(itemId).get().getOwner().getId()) {
            throw new NotOwnerException("The item can be deleted only by the owner");
        }
        repository.deleteById(itemId);
        log.info("The item with id {} deleted", itemId);
    }

    @Override
    public List<ItemDto> getSearchableItem(String text, int from, int size) {
        Pageable pageable = MyPageable.of(from, size);
        if (text.isBlank()) {
            return new ArrayList<>();
        }
        return repository.search(text, pageable).stream()
                .map(item -> mapper.toItemDto(item))
                .collect(Collectors.toList());
    }

    @Override
    public List<ItemDto> getItemsOfUser(long userId, int from, int size) {
        userValidation(userId);
        Pageable pageable = MyPageable.of(from, size);
        return repository.findByOwnerId(userId, pageable).stream()
                .map(mapper::toItemDto)
                .map(this::fillBookingInItemDto)
                .map(this::fillCommentsToItemDto)
                .sorted((o1, o2) -> (int) (o1.getId() - o2.getId()))
                .collect(Collectors.toList());
    }

    private void itemValidation(long itemId) {
        if (repository.findById(itemId).isEmpty()) {
            throw new ItemNotFoundException("This item is not found");
        }
    }

    public void userValidation(long userId) {
        if (userRepository.findById(userId).isEmpty()) {
            throw new UserNotFoundException("This user is not found");
        }
    }

    private void itemRequestValidation(long id) {
        if (itemRequestRepository.findById(id).isEmpty()) {
            throw new ItemRequestNotFoundException("This itemRequest is not found");
        }
    }

    @Override
    public Item getItem(long id) {
        return repository.findById(id).get();
    }

    @Override
    public CommentDto createNewComment(long userId, long itemId, CommentDto commentDto) {
        itemValidation(itemId);
        userValidation(userId);
        LocalDateTime now = LocalDateTime.now();
        List<Booking> bookings = bookingRepository.findAllByItemId(itemId);
        if (bookings.isEmpty() || bookings.stream()
                .noneMatch(booking -> booking.getBooker().getId() == userId)) {
            throw new BookingValidateException("You can not post a comment, because you don't book this item");
        }
        if (bookings.stream()
                .filter(booking -> booking.getBooker().getId() == userId)
                .noneMatch(booking -> booking.getEnd().isBefore(now))) {
            throw new BookingValidateException("You can not post a comment, because your booking of this item is not over yet");
        }
        Comment comment = CommentMapper.toComment(commentDto);
        comment.setItem(getItem(itemId));
        comment.setAuthor(userRepository.findById(userId).get());
        comment.setCreated(now);
        log.info("The соmment for item with id {} was added", itemId);
        return CommentMapper.toCommentDto(commentRepository.save(comment));
    }

    private ItemDto fillBookingInItemDto(ItemDto itemDto) {
        long itemId = itemDto.getId();
        List<BookingDto> bookings = bookingRepository.findAllByItemId(itemId).stream()
                .map(bookingMapper::toBookingDto)
                .sorted((o1, o2) -> o2.getStart().compareTo(o1.getStart()))
                .collect(Collectors.toList());
        for (int i = bookings.size() - 1; i >= 0; i--) {
            if (bookings.get(i).getStart().isAfter(LocalDateTime.now())) {
                itemDto.setNextBooking(bookingForItemDtoMapper.toBookingForItemDto(bookings.get(i)));
                if (i != bookings.size() - 1) {
                    itemDto.setLastBooking(bookingForItemDtoMapper.toBookingForItemDto(bookings.get(i + 1)));
                }
            }
        }
        return itemDto;
    }

    private ItemDto fillCommentsToItemDto(ItemDto itemDto) {
        if (!commentRepository.findByItemId(itemDto.getId()).isEmpty()) {
            itemDto.setComments(commentRepository.findByItemId(itemDto.getId()).stream()
                    .map(CommentMapper::toCommentDto)
                    .collect(Collectors.toList()));
        }
        return itemDto;
    }

    public void setMapper(ItemMapper mapper) {
        this.mapper = mapper;
    }
}
