package ru.practicum.shareit.booking.exception;

import lombok.AccessLevel;
import lombok.experimental.FieldDefaults;

@FieldDefaults(makeFinal = true, level = AccessLevel.PRIVATE)
public class BookingErrorResponse {
    String error;

    public BookingErrorResponse(String error) {
        this.error = error;
    }

    public String getError() {
        return error;
    }
}