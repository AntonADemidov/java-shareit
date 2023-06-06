package ru.practicum.shareit.item.dto;

import lombok.AccessLevel;
import lombok.Data;
import lombok.experimental.FieldDefaults;
import lombok.experimental.NonFinal;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

@Data
@FieldDefaults(makeFinal = true, level = AccessLevel.PRIVATE)
public class ItemDtoFromUserCreation {
    @NotBlank
    String name;
    @NotNull
    String description;
    @NotNull
    Boolean available;
    @NonFinal
    Long requestId;
}