package ru.practicum.shareit.booking.dto;

import lombok.AccessLevel;
import lombok.Data;
import lombok.experimental.FieldDefaults;
import ru.practicum.shareit.booking.model.Status;
import ru.practicum.shareit.item.dto.Item;
import ru.practicum.shareit.user.dto.User;

import java.time.LocalDateTime;

@Data
@FieldDefaults(level = AccessLevel.PRIVATE)
public class BookingDtoFromUser {
    Long itemId;
    LocalDateTime start;
    LocalDateTime end;
    Item item;
    User booker;
    Status status;

    public BookingDtoFromUser(Long itemId, LocalDateTime start, LocalDateTime end, Item item, User booker, Status status) {
        this.itemId = itemId;
        this.start = start;
        this.end = end;
        this.item = item;
        this.booker = booker;
        this.status = status;
    }
}