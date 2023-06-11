package ru.practicum.shareit.booking.exception;

import lombok.AccessLevel;
import lombok.experimental.FieldDefaults;

@FieldDefaults(makeFinal = true, level = AccessLevel.PRIVATE)
public class BookingValidationException extends Exception {
    String error;

    public BookingValidationException(String message) {
        super(message);
        this.error = message;
    }

    public String getError() {
        return error;
    }
}