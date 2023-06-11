package ru.practicum.shareit.booking.dto;

import lombok.AccessLevel;
import lombok.Data;
import lombok.experimental.FieldDefaults;
import lombok.experimental.NonFinal;
import ru.practicum.shareit.booking.model.Status;
import ru.practicum.shareit.item.dto.Item;
import ru.practicum.shareit.user.dto.User;

import javax.validation.constraints.NotNull;
import java.time.LocalDateTime;

@Data
@FieldDefaults(makeFinal = true, level = AccessLevel.PRIVATE)
public class Booking {
    @NonFinal
    Long id;
    @NotNull
    LocalDateTime start;
    @NotNull
    LocalDateTime end;
    @NotNull
    Item item;
    @NotNull
    User booker;
    @NonFinal
    Status status;
}