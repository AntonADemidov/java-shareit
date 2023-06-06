package ru.practicum.shareit.item.dto;

import lombok.AccessLevel;
import lombok.Data;
import lombok.experimental.FieldDefaults;
import ru.practicum.shareit.booking.dto.BookingDtoForUser;
import ru.practicum.shareit.item.comment.CommentDtoForUser;
import ru.practicum.shareit.user.dto.User;

import java.util.List;

@Data
@FieldDefaults(level = AccessLevel.PRIVATE)
public class ItemDtoForUser {
    Long id;
    String name;
    String description;
    Boolean available;
    User owner;
    Long requestId;
    BookingDtoForUser lastBooking;
    BookingDtoForUser nextBooking;
    List<CommentDtoForUser> comments;
}