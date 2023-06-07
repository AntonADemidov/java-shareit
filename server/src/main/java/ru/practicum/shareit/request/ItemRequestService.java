package ru.practicum.shareit.request;

import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.request.dto.ItemRequestDtoForUser;
import ru.practicum.shareit.request.dto.ItemRequestDtoFromUser;

import java.util.Collection;

@Transactional(readOnly = true)
public interface ItemRequestService {
    @Transactional
    ItemRequestDtoForUser createItemRequest(Long userId, ItemRequestDtoFromUser itemRequestDtoFromUser);

    Collection<ItemRequestDtoForUser> getOwnItemRequests(Long userId);

    Collection<ItemRequestDtoForUser> getAllItemRequests(Long userId, Integer from, Integer size);

    ItemRequestDtoForUser getItemRequestById(Long userId, Long id);

    @Transactional
    void deleteAll();
}
