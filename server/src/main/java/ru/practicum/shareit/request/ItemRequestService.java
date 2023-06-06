package ru.practicum.shareit.request;

import ru.practicum.shareit.request.dto.ItemRequestDtoForUser;
import ru.practicum.shareit.request.dto.ItemRequestDtoFromUser;

import java.util.Collection;

public interface ItemRequestService {
    ItemRequestDtoForUser createItemRequest(Long userId, ItemRequestDtoFromUser itemRequestDtoFromUser);

    Collection<ItemRequestDtoForUser> getOwnItemRequests(Long userId);

    Collection<ItemRequestDtoForUser> getAllItemRequests(Long userId, Integer from, Integer size);

    ItemRequestDtoForUser getItemRequestById(Long userId, Long id);

    void deleteAll();
}
