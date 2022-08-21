package ru.practicum.shareit.booking.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.model.BookingStatus;

import java.time.LocalDateTime;
import java.util.List;

public interface BookingRepository extends JpaRepository<Booking, Long> {
    List<Booking> findByBookerId(long bookerId);

    List<Booking> findAllByItemId(long itemId);

    @Query(value = "SELECT * " +
            "FROM bookings AS b " +
            "JOIN items AS i ON b.item_id=i.id " +
            "WHERE i.owner_id = ?1",
            nativeQuery = true)
    List<Booking> getBookingsByItemOwnerId(long userId);

    List<Booking> findAllByBookerIdOrderByStartDesc(long userId);

    List<Booking> findAllByBookerIdAndStartBeforeAndEndAfterOrderByStartDesc(long userId, LocalDateTime startFilter, LocalDateTime endFilter);

    List<Booking> findAllByBookerIdAndEndBeforeOrderByStartDesc(long userId, LocalDateTime endFilter);

    List<Booking> findAllByBookerIdAndStartAfterOrderByStartDesc(long userId, LocalDateTime startFilter);

    List<Booking> findAllByBookerIdAndStatusOrderByStartDesc(long userId, BookingStatus status);

    List<Booking> findAllByItemOwnerIdOrderByStartDesc(long userId);

    List<Booking> findAllByItemOwnerIdAndStartBeforeAndEndAfterOrderByStartDesc(long userId, LocalDateTime startFilter, LocalDateTime endFilter);

    List<Booking> findAllByItemOwnerIdAndEndBeforeOrderByStartDesc(long userId, LocalDateTime endFilter);

    List<Booking> findAllByItemOwnerIdAndStartAfterOrderByStartDesc(long userId, LocalDateTime startFilter);

    List<Booking> findAllByItemOwnerIdAndStatusOrderByStartDesc(long userId, BookingStatus status);
}
