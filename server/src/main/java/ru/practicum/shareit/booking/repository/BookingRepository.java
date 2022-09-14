package ru.practicum.shareit.booking.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
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

    Page<Booking> findAllByBookerIdOrderByStartDesc(long userId, Pageable pageable);

    Page<Booking> findAllByBookerIdAndStartBeforeAndEndAfterOrderByStartDesc(long userId, LocalDateTime startFilter,
                                                                             LocalDateTime endFilter, Pageable pageable);

    Page<Booking> findAllByBookerIdAndEndBeforeOrderByStartDesc(long userId, LocalDateTime endFilter, Pageable pageable);

    Page<Booking> findAllByBookerIdAndStartAfterOrderByStartDesc(long userId, LocalDateTime startFilter, Pageable pageable);

    Page<Booking> findAllByBookerIdAndStatusOrderByStartDesc(long userId, BookingStatus status, Pageable pageable);

    Page<Booking> findAllByItemOwnerIdOrderByStartDesc(long userId, Pageable pageable);

    Page<Booking> findAllByItemOwnerIdAndStartBeforeAndEndAfterOrderByStartDesc(long userId, LocalDateTime startFilter,
                                                                                LocalDateTime endFilter, Pageable pageable);

    Page<Booking> findAllByItemOwnerIdAndEndBeforeOrderByStartDesc(long userId, LocalDateTime endFilter, Pageable pageable);

    Page<Booking> findAllByItemOwnerIdAndStartAfterOrderByStartDesc(long userId, LocalDateTime startFilter, Pageable pageable);

    Page<Booking> findAllByItemOwnerIdAndStatusOrderByStartDesc(long userId, BookingStatus status, Pageable pageable);
}
