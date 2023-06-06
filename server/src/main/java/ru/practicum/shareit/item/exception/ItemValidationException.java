package ru.practicum.shareit.item.exception;

import lombok.AccessLevel;
import lombok.experimental.FieldDefaults;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@FieldDefaults(makeFinal = true, level = AccessLevel.PRIVATE)
@ResponseStatus(HttpStatus.BAD_REQUEST)
public class ItemValidationException extends Exception {
    public ItemValidationException(String message) {
        super(message);
    }
}