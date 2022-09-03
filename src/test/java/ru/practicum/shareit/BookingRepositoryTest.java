package ru.practicum.shareit;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.data.domain.Pageable;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.model.BookingStatus;
import ru.practicum.shareit.booking.repository.BookingRepository;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.model.User;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;


@DataJpaTest
@Transactional
public class BookingRepositoryTest {
    @Autowired
    TestEntityManager em;
    @Autowired
    BookingRepository bookingRepository;
    private final User user = new User();
    private final User user1 = new User();
    private final Item item = new Item();
    private final Booking booking = new Booking();
    Pageable pageable = MyPageable.of(0, 20);

    @BeforeEach
    void setUp() {
        user.setName("Tom");
        user.setEmail("Tom@gmail.com");
        em.persist(user);
        item.setName("test");
        item.setDescription("description");
        item.setAvailable(true);
        item.setOwner(user);
        em.persist(item);
        user1.setName("Tim");
        user1.setEmail("Tim@mail.com");
        em.persist(user1);
        booking.setStart(LocalDateTime.now().plusDays(2));
        booking.setEnd(LocalDateTime.now().plusDays(5));
        booking.setItem(item);
        booking.setBooker(user1);
        booking.setStatus(BookingStatus.APPROVED);
        em.persist(booking);
    }

    @Test
    void findAllByOwnerIdOrderByEndDesc() {
        List<Booking> bookings = bookingRepository.findAllByItemOwnerIdOrderByStartDesc(user.getId(), pageable).stream().collect(Collectors.toList());
        Assertions.assertEquals(1, bookings.size());
        Assertions.assertEquals(booking, bookings.get(0));
    }

    @Test
    void findAllByOwnerIdAndStatusOrderByEndDesc() {
        List<Booking> bookings = bookingRepository.findAllByItemOwnerIdAndStatusOrderByStartDesc(user.getId(),
                BookingStatus.APPROVED, pageable).stream().collect(Collectors.toList());
        Assertions.assertEquals(1, bookings.size());
        Assertions.assertEquals(booking, bookings.get(0));
    }

    @Test
    void findAllByBookerIdOrderByStartDesc() {
        List<Booking> bookings = bookingRepository.findAllByBookerIdOrderByStartDesc(user1.getId(), pageable).stream().collect(Collectors.toList());
        Assertions.assertEquals(1, bookings.size());
        Assertions.assertEquals(booking, bookings.get(0));
    }

    @Test
    void findAllByBookerIdAndStatusOrderByStartDesc() {
        List<Booking> bookings = bookingRepository.findAllByBookerIdAndStatusOrderByStartDesc(user1.getId(),
                BookingStatus.APPROVED, pageable).stream().collect(Collectors.toList());
        Assertions.assertEquals(1, bookings.size());
        Assertions.assertEquals(booking, bookings.get(0));
    }

}