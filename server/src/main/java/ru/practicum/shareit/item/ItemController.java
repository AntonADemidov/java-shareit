package ru.practicum.shareit.item;

import lombok.AccessLevel;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.item.comment.CommentDtoForUser;
import ru.practicum.shareit.item.comment.CommentDtoFromUser;
import ru.practicum.shareit.item.dto.Item;
import ru.practicum.shareit.item.dto.ItemDtoForUser;
import ru.practicum.shareit.item.dto.ItemDtoFromUser;
import ru.practicum.shareit.item.dto.ItemDtoFromUserCreation;
import ru.practicum.shareit.item.exception.ItemValidationException;

import java.util.Collection;

@RestController
@RequestMapping(path = "/items")
@Slf4j
@FieldDefaults(makeFinal = true, level = AccessLevel.PRIVATE)
public class ItemController {
    ItemService itemService;
    private static final String actionWithId = "/{id}";
    private static final String userHeader = "X-Sharer-User-Id";

    @Autowired
    public ItemController(ItemServiceImpl itemService) {
        this.itemService = itemService;
    }

    @PostMapping
    public ItemDtoForUser createItem(@RequestHeader(userHeader) Long userId,
                                     @RequestBody ItemDtoFromUserCreation itemDto) throws Exception {
        return itemService.createItem(userId, itemDto);
    }

    @PatchMapping(actionWithId)
    public ItemDtoForUser updateItem(@RequestHeader(userHeader) Long userId,
                                     @RequestBody ItemDtoFromUser itemDto,
                                     @PathVariable Long id) throws Exception {
        return itemService.updateItem(userId, itemDto, id);
    }

    @GetMapping(actionWithId)
    public ItemDtoForUser getItemById(@RequestHeader(userHeader) Long userId,
                                      @PathVariable Long id) {
        return itemService.getItemDtoForUserById(userId, id);
    }

    @GetMapping
    public Collection<ItemDtoForUser> getItemsOfUser(@RequestHeader(userHeader) Long userId,
                                    @RequestParam(value = "from", defaultValue = "0", required = false) Integer from,
                                    @RequestParam(value = "size", defaultValue = "20", required = false) Integer size) {
        return itemService.getItemsOfUser(userId, from, size);
    }

    @GetMapping("/search")
    public Collection<Item> searchItems(@RequestParam(value = "text", defaultValue = "", required = false) String text,
                                    @RequestParam(value = "from", defaultValue = "0", required = false) Integer from,
                                    @RequestParam(value = "size", defaultValue = "20", required = false) Integer size) {
        return itemService.searchItems(text, from, size);
    }

    @DeleteMapping(actionWithId)
    public void deleteItem(@PathVariable Long id) {
        itemService.deleteItem(id);
    }

    @PostMapping("/{id}/comment")
    public CommentDtoForUser addComment(@RequestHeader(userHeader) Long userId,
                                        @RequestBody CommentDtoFromUser commentDtoFromUser,
                                        @PathVariable Long id) throws ItemValidationException {
        return itemService.addComment(userId, commentDtoFromUser, id, null);
    }
}