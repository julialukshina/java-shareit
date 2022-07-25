package ru.practicum.shareit.booking.model;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

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
    private long itemId;
    private  long bookerId;
    private BOOKING_STATUS bookingStatus;

    public Booking(long id, LocalDate start, LocalDate end, long itemId, long bookerId, BOOKING_STATUS bookingStatus) {
        this.id = id;
        this.start = start;
        this.end = end;
        this.itemId = itemId;
        this.bookerId = bookerId;
        this.bookingStatus = bookingStatus;
    }
}
