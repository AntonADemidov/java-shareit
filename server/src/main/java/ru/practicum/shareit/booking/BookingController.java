package ru.practicum.shareit.booking;

import lombok.AccessLevel;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.booking.dto.Booking;
import ru.practicum.shareit.booking.dto.BookingDtoFromUser;
import ru.practicum.shareit.booking.exception.BookingValidationException;
import ru.practicum.shareit.booking.model.GetBookingRequest;
import ru.practicum.shareit.booking.model.State;

import java.util.Collection;

@RestController
@RequestMapping(path = "/bookings")
@Slf4j
@FieldDefaults(makeFinal = true, level = AccessLevel.PRIVATE)
public class BookingController {
    BookingService bookingService;
    private static final String actionWithId = "/{id}";
    static final String userHeader = "X-Sharer-User-Id";

    @Autowired
    public BookingController(BookingServiceImpl bookingService) {
        this.bookingService = bookingService;
    }

    @PostMapping
    public Booking createBooking(@RequestHeader(userHeader) Long userId,
                                 @RequestBody BookingDtoFromUser bookingDtoFromUser) throws BookingValidationException {
        return bookingService.createBooking(userId, bookingDtoFromUser);
    }

    @PatchMapping(actionWithId)
    public Booking updateBookingStatus(@RequestHeader(userHeader) Long userId,
                                       @PathVariable Long id,
                                       @RequestParam(value = "approved") Boolean value) throws Exception {
        return bookingService.updateBookingStatus(userId, id, value);
    }

    @GetMapping(actionWithId)
    public Booking getBookingById(@RequestHeader(userHeader) Long userId,
                                  @PathVariable Long id) throws BookingValidationException {
        return bookingService.getBookingById(userId, id);
    }

    @GetMapping
    public Collection<Booking> getBookingsOfUser(@RequestHeader(userHeader) Long userId,
                                @RequestParam(value = "state", defaultValue = "ALL", required = false) State state,
                                @RequestParam(value = "from", defaultValue = "0", required = false) Integer from,
                                @RequestParam(value = "size", defaultValue = "20", required = false) Integer size) {
        return bookingService.getBookingsOfUser(GetBookingRequest.of(userId, state, from, size));
    }

    @GetMapping("/owner")
    public Collection<Booking> getBookingsOfItemsOfUser(@RequestHeader(userHeader) Long userId,
                                @RequestParam(value = "state", defaultValue = "ALL", required = false) State value,
                                @RequestParam(value = "from", defaultValue = "0", required = false) Integer from,
                                @RequestParam(value = "size", defaultValue = "20", required = false) Integer size) {
        return bookingService.getBookingsOfItemsOfUser(GetBookingRequest.of(userId, value, from, size));
    }
}