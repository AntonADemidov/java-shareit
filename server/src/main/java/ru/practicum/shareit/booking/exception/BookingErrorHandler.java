package ru.practicum.shareit.booking.exception;

import lombok.AccessLevel;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
@Slf4j
@FieldDefaults(makeFinal = true, level = AccessLevel.PRIVATE)
public class BookingErrorHandler {

    @ExceptionHandler
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public BookingErrorResponse handleValidationException(final BookingValidationException e) {
        log.info("400 {}", e.getMessage());
        return new BookingErrorResponse(e.getMessage());
    }

    @ExceptionHandler
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public BookingErrorResponse handleBookingNotFoundException(final BookingNotFoundException e) {
        log.info("404 {}", e.getMessage());
        return new BookingErrorResponse(e.getMessage());
    }
}