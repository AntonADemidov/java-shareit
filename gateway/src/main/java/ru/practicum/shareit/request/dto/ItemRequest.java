package ru.practicum.shareit.request.dto;

import lombok.AccessLevel;
import lombok.Data;
import lombok.experimental.FieldDefaults;
import lombok.experimental.NonFinal;
import ru.practicum.shareit.user.dto.User;

import java.time.LocalDateTime;

@Data
@FieldDefaults(makeFinal = true, level = AccessLevel.PRIVATE)
public class ItemRequest {
    @NonFinal
    Long id;
    String description;
    User requester;
    LocalDateTime created;
}