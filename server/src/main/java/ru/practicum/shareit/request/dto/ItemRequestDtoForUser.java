package ru.practicum.shareit.request.dto;

import lombok.AccessLevel;
import lombok.Data;
import lombok.experimental.FieldDefaults;
import lombok.experimental.NonFinal;
import ru.practicum.shareit.item.dto.ItemDtoForUser;

import java.time.LocalDateTime;
import java.util.List;

@Data
@FieldDefaults(makeFinal = true, level = AccessLevel.PRIVATE)
public class ItemRequestDtoForUser {
    Long id;
    String description;
    LocalDateTime created;
    @NonFinal
    List<ItemDtoForUser> items;
}