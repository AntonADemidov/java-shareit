package ru.practicum.shareit.item.dto;

import lombok.AccessLevel;
import lombok.Data;
import lombok.experimental.FieldDefaults;
import ru.practicum.shareit.request.dto.ItemRequest;
import ru.practicum.shareit.user.dto.User;

import javax.validation.constraints.NotNull;

@Data
@FieldDefaults(level = AccessLevel.PRIVATE)
public class Item {
    Long id;
    @NotNull
    final String name;
    @NotNull
    final String description;
    @NotNull
    final Boolean available;
    @NotNull
    User owner;
    ItemRequest request;
}