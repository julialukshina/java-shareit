package ru.practicum.shareit.booking.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import ru.practicum.shareit.booking.model.Booking;

import java.util.List;

public interface BookingRepository extends JpaRepository<Booking, Long> {
    List<Booking> findByBookerId(long bookerId);

    List<Booking> findAllByItemId(long itemId);

    @Query(value = "SELECT * " +
            "FROM bookings AS b " +
            "JOIN items AS i ON b.item_id=i.id " +
            "WHERE i.owner_id = ?1",
            nativeQuery = true)
    List<Booking> getBookingsForItemOwner(long userId);
}
