package ru.practicum.shareit.item;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.item.comment.CommentDtoFromUser;
import ru.practicum.shareit.item.dto.ItemDtoFromUser;
import ru.practicum.shareit.item.dto.ItemDtoFromUserCreation;

import javax.validation.Valid;
import javax.validation.constraints.Positive;
import javax.validation.constraints.PositiveOrZero;

@RestController
@RequestMapping(path = "/items")
@RequiredArgsConstructor
@Slf4j
@Validated
@FieldDefaults(makeFinal = true, level = AccessLevel.PRIVATE)
public class ItemController {
    ItemClient itemClient;
    static final String actionWithId = "/{id}";
    static final String userHeader = "X-Sharer-User-Id";

    @PostMapping
    public ResponseEntity<Object> createItem(@RequestHeader(userHeader) @Positive Long userId,
                                             @RequestBody @Valid ItemDtoFromUserCreation itemDto) {
        log.info("Creating item {}, userId={}", itemDto, userId);
        return itemClient.createItem(userId, itemDto);
    }

    @PatchMapping(actionWithId)
    public ResponseEntity<Object> updateItem(@RequestHeader(userHeader) @Positive Long userId,
                                             @RequestBody ItemDtoFromUser itemDto,
                                             @PathVariable @Positive Long id) {
        log.info("Updating item {}, itemId={}, userId={}", itemDto, id, userId);
        return itemClient.updateItem(userId, itemDto, id);
    }

    @GetMapping(actionWithId)
    public ResponseEntity<Object> getItemById(@RequestHeader(userHeader) @Positive Long userId,
                                              @PathVariable @Positive Long id) {
        log.info("Getting item with itemId={}, userId={}", id, userId);
        return itemClient.getItemDtoForUserById(userId, id);
    }

    @GetMapping
    public ResponseEntity<Object> getItemsOfUser(@RequestHeader(userHeader) @Positive Long userId,
                    @RequestParam(value = "from", defaultValue = "0", required = false) @PositiveOrZero Integer from,
                    @RequestParam(value = "size", defaultValue = "20", required = false) @Positive Integer size) {
        log.info("Getting items of user with userId={} (params: from={}, size={})", userId, from, size);
        return itemClient.getItemsOfUser(userId, from, size);
    }

    @GetMapping("/search")
    public ResponseEntity<Object> searchItems(@RequestParam(value = "text", defaultValue = "", required = false) String text,
                            @RequestParam(value = "from", defaultValue = "0", required = false) @PositiveOrZero Integer from,
                            @RequestParam(value = "size", defaultValue = "20", required = false) @Positive Integer size) {
        log.info("Searching items with text={} in fields name or description (params: from={}, size={})", text, from, size);
        return itemClient.searchItems(text, from, size);
    }

    @DeleteMapping(actionWithId)
    public ResponseEntity<Object> deleteItem(@PathVariable @Positive Long id) {
        return itemClient.deleteItem(id);
    }

    @PostMapping("/{id}/comment")
    public ResponseEntity<Object> addComment(@RequestHeader(userHeader) @Positive Long userId,
                                             @RequestBody @Valid CommentDtoFromUser commentDtoFromUser,
                                             @PathVariable @Positive Long id) {
        log.info("Creating comment {}, userId={}, itemId={}", commentDtoFromUser, userId, id);
        return itemClient.addComment(userId, commentDtoFromUser, id);
    }
}