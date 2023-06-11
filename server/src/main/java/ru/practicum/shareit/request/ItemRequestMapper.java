package ru.practicum.shareit.request;

import ru.practicum.shareit.request.dto.ItemRequest;
import ru.practicum.shareit.request.dto.ItemRequestDtoForUser;
import ru.practicum.shareit.request.dto.ItemRequestDtoFromUser;
import ru.practicum.shareit.user.dto.User;

import java.time.LocalDateTime;

public class ItemRequestMapper {
    public static ItemRequest toItemRequest(ItemRequestDtoFromUser itemRequestDtoFromUser, User user, LocalDateTime moment) {
        return new ItemRequest(itemRequestDtoFromUser.getDescription(), user, moment);
    }

    public static ItemRequestDtoForUser toItemRequestDtoForUser(ItemRequest newItemRequest) {
        return new ItemRequestDtoForUser(newItemRequest.getId(), newItemRequest.getDescription(), newItemRequest.getCreated());
    }

    public static ItemRequest toItemRequest(ItemRequestDtoForUser itemRequestDto, User user) {
        ItemRequest itemRequest = new ItemRequest(itemRequestDto.getDescription(), user, itemRequestDto.getCreated());
        itemRequest.setId(itemRequestDto.getId());
        return itemRequest;
    }
}