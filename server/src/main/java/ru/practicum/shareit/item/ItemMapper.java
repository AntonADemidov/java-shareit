package ru.practicum.shareit.item;

import ru.practicum.shareit.booking.dto.BookingDtoForUser;
import ru.practicum.shareit.item.comment.CommentDtoForUser;
import ru.practicum.shareit.item.dto.Item;
import ru.practicum.shareit.item.dto.ItemDtoForUser;
import ru.practicum.shareit.item.dto.ItemDtoFromUser;
import ru.practicum.shareit.item.dto.ItemDtoFromUserCreation;

import java.util.List;

public class ItemMapper {
    public static Item toItem(ItemDtoFromUser itemDto) {
        return new Item(itemDto.getName(), itemDto.getDescription(), itemDto.getAvailable());
    }

    public static ItemDtoForUser toItemDtoForUser(Item item, Long requestId, BookingDtoForUser lastBooking,
                                                  BookingDtoForUser nextBooking, List<CommentDtoForUser> comments) {
        return new ItemDtoForUser(item.getId(), item.getName(), item.getDescription(), item.getAvailable(),
                item.getOwner(), requestId, lastBooking, nextBooking, comments);
    }

    public static Item toItem(ItemDtoForUser itemDto) {
        Item item = new Item(itemDto.getName(), itemDto.getDescription(), itemDto.getAvailable(),
                itemDto.getOwner());
        item.setId(itemDto.getId());
        return item;
    }

    public static Item toItem(ItemDtoFromUserCreation itemDto) {
        return new Item(itemDto.getName(), itemDto.getDescription(), itemDto.getAvailable());
    }
}