package ru.practicum.shareit.booking;

import ru.practicum.shareit.booking.dto.Booking;
import ru.practicum.shareit.booking.dto.BookingDtoFromUser;
import ru.practicum.shareit.booking.exception.BookingValidationException;
import ru.practicum.shareit.booking.model.GetBookingRequest;

import java.util.Collection;

public interface BookingService {
    Booking createBooking(Long userId, BookingDtoFromUser bookingDtoFromUser) throws BookingValidationException;

    Booking updateBookingStatus(Long userId, Long id, Boolean value) throws BookingValidationException;

    Booking getBookingById(Long userId, Long id) throws BookingValidationException;

    Collection<Booking> getBookingsOfUser(GetBookingRequest request);

    Collection<Booking> getBookingsOfItemsOfUser(GetBookingRequest request);

    void deleteAll();
}