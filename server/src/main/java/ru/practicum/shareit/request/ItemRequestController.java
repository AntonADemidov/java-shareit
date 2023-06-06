package ru.practicum.shareit.request;

import lombok.AccessLevel;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.request.dto.ItemRequestDtoForUser;
import ru.practicum.shareit.request.dto.ItemRequestDtoFromUser;

import java.util.Collection;

@RestController
@RequestMapping(path = "/requests")
@Slf4j
@FieldDefaults(makeFinal = true, level = AccessLevel.PRIVATE)
public class ItemRequestController {
    ItemRequestService itemRequestService;
    private static final String userHeader = "X-Sharer-User-Id";

    @Autowired
    public ItemRequestController(ItemRequestServiceImpl itemRequestService) {
        this.itemRequestService = itemRequestService;
    }

    @PostMapping
    public ItemRequestDtoForUser createItemRequest(@RequestHeader(userHeader) Long userId,
                                                   @RequestBody ItemRequestDtoFromUser itemRequestDtoFromUser) {
        return itemRequestService.createItemRequest(userId, itemRequestDtoFromUser);
    }

    @GetMapping
    public Collection<ItemRequestDtoForUser> getOwnItemRequests(@RequestHeader(userHeader) Long userId) {
        return itemRequestService.getOwnItemRequests(userId);
    }

    @GetMapping("/all")
    public Collection<ItemRequestDtoForUser> getAllItemRequests(@RequestHeader(userHeader) Long userId,
                                   @RequestParam(value = "from", defaultValue = "0", required = false) Integer from,
                                   @RequestParam(value = "size", defaultValue = "20", required = false) Integer size) {
        return itemRequestService.getAllItemRequests(userId, from, size);
    }

    @GetMapping("/{id}")
    public ItemRequestDtoForUser getItemRequestById(@RequestHeader(userHeader) Long userId,
                                                    @PathVariable Long id) {
        return itemRequestService.getItemRequestById(userId, id);
    }
}