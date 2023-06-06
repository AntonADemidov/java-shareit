package ru.practicum.shareit.item.dto;

import lombok.AccessLevel;
import lombok.Data;
import lombok.experimental.FieldDefaults;
import lombok.experimental.NonFinal;

@Data
@FieldDefaults(makeFinal = true, level = AccessLevel.PRIVATE)
public class ItemDtoFromUserCreation {
    String name;
    String description;
    Boolean available;
    @NonFinal
    Long requestId;

    public ItemDtoFromUserCreation(String name, String description, Boolean available, Long requestId) {
        this.name = name;
        this.description = description;
        this.available = available;
        this.requestId = requestId;
    }
}