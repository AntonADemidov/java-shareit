package ru.practicum.shareit.booking;

import ru.practicum.shareit.booking.dto.Booking;
import ru.practicum.shareit.booking.dto.BookingDtoForUser;
import ru.practicum.shareit.booking.dto.BookingDtoFromUser;

public class BookingMapper {
    public static Booking toBooking(BookingDtoFromUser bookingDtoFromUser) {
        return new Booking(bookingDtoFromUser.getStart(), bookingDtoFromUser.getEnd(),
                bookingDtoFromUser.getItem(), bookingDtoFromUser.getBooker(), bookingDtoFromUser.getStatus());
    }

    public static BookingDtoForUser toBookingDtoForUser(Booking booking) {
        return new BookingDtoForUser(booking.getId(), booking.getBooker().getId());
    }
}