package ru.practicum.shareit.request.exception;

import lombok.AccessLevel;
import lombok.experimental.FieldDefaults;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@FieldDefaults(makeFinal = true, level = AccessLevel.PRIVATE)
@ResponseStatus(HttpStatus.NOT_FOUND)
public class ItemRequestNotFoundException extends RuntimeException {
    public ItemRequestNotFoundException(String message) {
        super(message);
    }
}