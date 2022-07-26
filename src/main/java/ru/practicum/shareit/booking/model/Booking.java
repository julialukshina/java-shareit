package ru.practicum.shareit.booking.model;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.model.User;

import javax.validation.constraints.Future;
import java.time.LocalDate;

@Getter
@Setter
@ToString
@EqualsAndHashCode
public class Booking {

    private long id;
    @Future
    private LocalDate start;
    @Future
    private LocalDate end;
    private Item item;
    private User booker;
    private BookingStatus bookingStatus;

    public Booking(long id, LocalDate start, LocalDate end, Item item, User booker, BookingStatus bookingStatus) {
        this.id = id;
        this.start = start;
        this.end = end;
        this.item = item;
        this.booker = booker;
        this.bookingStatus = bookingStatus;
    }
}
