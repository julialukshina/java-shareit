package ru.practicum.shareit.booking.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.MyPageable;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.dto.BookingShortDto;
import ru.practicum.shareit.booking.mapper.BookingMapper;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.model.BookingStatus;
import ru.practicum.shareit.booking.model.State;
import ru.practicum.shareit.booking.repository.BookingRepository;
import ru.practicum.shareit.exception.ParamException;
import ru.practicum.shareit.exception.bookings.BookerException;
import ru.practicum.shareit.exception.bookings.BookingNotFoundException;
import ru.practicum.shareit.exception.bookings.BookingValidateException;
import ru.practicum.shareit.exception.items.NotOwnerException;
import ru.practicum.shareit.exception.items.OwnerException;
import ru.practicum.shareit.exception.users.UserNotFoundException;
import ru.practicum.shareit.item.mapper.ItemMapper;
import ru.practicum.shareit.item.repository.ItemRepository;
import ru.practicum.shareit.user.mapper.UserMapper;
import ru.practicum.shareit.user.repository.UserRepository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@Slf4j
public class BookingServiceImpl implements BookingService {
    @Lazy
    @Autowired
    private BookingMapper mapper;
    @Lazy
    @Autowired
    private ItemMapper itemMapper;
    private final UserRepository userRepository;
    private final ItemRepository itemRepository;
    private final BookingRepository repository;

    @Autowired
    public BookingServiceImpl(UserRepository userRepository, ItemRepository itemRepository, BookingRepository repository) {
        this.userRepository = userRepository;
        this.itemRepository = itemRepository;
        this.repository = repository;
    }

    @Override
    public BookingDto createNewBooking(long userId, BookingShortDto shortDto) { //создание нового бронирования
        userValidation(userId);
        itemValidation(shortDto.getItemId());
        if (itemRepository.findById(shortDto.getItemId()).get().getAvailable() == false) {
            throw new BookingValidateException("Only available item can be booked");
        }
        if (shortDto.getStart().isAfter(shortDto.getEnd())) {
            throw new BookingValidateException("The end of booking can not be before it's start");
        }
        if (userId == itemRepository.findById(shortDto.getItemId()).get().getOwner().getId()) {
            throw new OwnerException("The owner can not book his item");
        }

        BookingDto dto = BookingDto.builder()
                .start(shortDto.getStart())
                .end(shortDto.getEnd())
                .item(itemMapper.toItemDto(itemRepository.findById(shortDto.getItemId()).get()))
                .booker(UserMapper.toUserDto(userRepository.findById(userId).get()))
                .status(BookingStatus.WAITING)
                .build();

        dto.setId(repository.save(mapper.toBooking(dto)).getId());
        log.info("The new booking has been created: {}", dto);
        return dto;
    }

    @Override
    public BookingDto updateBooking(long userId, long bookingId, Boolean isApproved) { //обновление статуса бронирования
        userValidation(userId);
        bookingValidation(bookingId);
        if (userId != repository.findById(bookingId).get().getItem().getOwner().getId()) {
            throw new NotOwnerException("Booking's status can changed only by the owner");
        }
        BookingDto dto = mapper.toBookingDto(repository.findById(bookingId).get());
        if (dto.getStatus() == BookingStatus.APPROVED) {
            throw new BookingValidateException("The booking is already approved");
        }
        if (isApproved == true) {
            dto.setStatus(BookingStatus.APPROVED);
        }
        if (isApproved == false) {
            dto.setStatus(BookingStatus.REJECTED);
        }
        repository.save(mapper.toBooking(dto));
        log.info("The booking with id {} was updated", bookingId);
        return dto;
    }

    @Override
    public BookingDto getBookingById(long userId, Long bookingId) { //получение брнирования по id
        userValidation(userId);
        bookingValidation(bookingId);
        if (userId != repository.findById(bookingId).get().getItem().getOwner().getId() &&
                userId != repository.findById(bookingId).get().getBooker().getId()) {
            throw new BookerException("Booking can get only by the owner or the booker");
        }
        return mapper.toBookingDto(repository.findById(bookingId).get());
    }

    @Override
    public List<BookingDto> getAllForBooker(long userId, String state, int from, int size) { //возвращает все бронирования для создателя бронирований
        userValidation(userId);
        if (from < 0 || size <= 0) {
            throw new ParamException("Param 'from' can't be negative, param 'size' can't be 0 or negative");
        }
        Pageable pageable = MyPageable.of(from, size);
        LocalDateTime now = LocalDateTime.now();
        try {
            State s = State.valueOf(state);
        } catch (IllegalArgumentException e) {
            e.getMessage();
        }
        switch (State.valueOf(state)) {
            case ALL:
                return repository.findAllByBookerIdOrderByStartDesc(userId, pageable).stream()
                        .map(mapper::toBookingDto)
                        .collect(Collectors.toList());
            case CURRENT:
                return repository.findAllByBookerIdAndStartBeforeAndEndAfterOrderByStartDesc(userId, now, now, pageable).stream()
                        .map(mapper::toBookingDto)
                        .collect(Collectors.toList());
            case PAST:
                return repository.findAllByBookerIdAndEndBeforeOrderByStartDesc(userId, now, pageable).stream()
                        .map(mapper::toBookingDto)
                        .collect(Collectors.toList());
            case FUTURE:
                return repository.findAllByBookerIdAndStartAfterOrderByStartDesc(userId, now, pageable).stream()
                        .map(mapper::toBookingDto)
                        .collect(Collectors.toList());
            case WAITING:
                return repository.findAllByBookerIdAndStatusOrderByStartDesc(userId, BookingStatus.WAITING, pageable).stream()
                        .map(mapper::toBookingDto)
                        .collect(Collectors.toList());
            case REJECTED:
                return repository.findAllByBookerIdAndStatusOrderByStartDesc(userId, BookingStatus.REJECTED, pageable).stream()
                        .map(mapper::toBookingDto)
                        .collect(Collectors.toList());
        }
        return null;
    }

    @Override
    public List<BookingDto> getAllForOwner(long userId, String state, int from, int size) { //возвращает все бронирования для собственника вещей
        userValidation(userId);
        if (from < 0 || size <= 0) {
            throw new ParamException("Param 'from' can't be negative, param 'size' can't be 0 or negative");
        }
        Pageable pageable = MyPageable.of(from, size);
        LocalDateTime now = LocalDateTime.now();
        List<Booking> bookings = repository.getBookingsByItemOwnerId(userId);
        if (bookings.isEmpty()) {
            throw new NotOwnerException("This user is not the owner for any item");
        }
        try {
            State s = State.valueOf(state);
        } catch (IllegalArgumentException e) {
            e.getMessage();
        }
        switch (State.valueOf(state)) {
            case ALL:
                return repository.findAllByItemOwnerIdOrderByStartDesc(userId, pageable).stream()
                        .map(mapper::toBookingDto)
                        .collect(Collectors.toList());
            case CURRENT:
                return repository.findAllByItemOwnerIdAndStartBeforeAndEndAfterOrderByStartDesc(userId, now, now, pageable).stream()
                        .map(mapper::toBookingDto)
                        .collect(Collectors.toList());
            case PAST:
                return repository.findAllByItemOwnerIdAndEndBeforeOrderByStartDesc(userId, now, pageable).stream()
                        .map(mapper::toBookingDto)
                        .collect(Collectors.toList());
            case FUTURE:
                return repository.findAllByItemOwnerIdAndStartAfterOrderByStartDesc(userId, now, pageable).stream()
                        .map(mapper::toBookingDto)
                        .collect(Collectors.toList());
            case WAITING:
                return repository.findAllByItemOwnerIdAndStatusOrderByStartDesc(userId, BookingStatus.WAITING, pageable).stream()
                        .map(mapper::toBookingDto)
                        .collect(Collectors.toList());
            case REJECTED:
                return repository.findAllByItemOwnerIdAndStatusOrderByStartDesc(userId, BookingStatus.REJECTED, pageable).stream()
                        .map(mapper::toBookingDto)
                        .collect(Collectors.toList());
        }
        return null;
    }


    private void bookingValidation(long bookingId) { //проверка наличия в репозитории бронирования с таким id
        if (repository.findById(bookingId).isEmpty()) {
            throw new BookingNotFoundException("This booking is not found");
        }
    }

    private void itemValidation(long itemId) { //проверка наличия в репозитории вещи с таким id
        if (itemRepository.findById(itemId).isEmpty()) {
            throw new BookingNotFoundException("This item is not found");
        }
    }

    private void userValidation(long userId) { //проверка наличия в репозитории пользователя с таким id
        if (userRepository.findById(userId).isEmpty()) {
            throw new UserNotFoundException("This user is not found");
        }
    }
}
