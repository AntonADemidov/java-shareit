package ru.practicum.shareit.item;

import ru.practicum.shareit.item.comment.CommentDtoForUser;
import ru.practicum.shareit.item.comment.CommentDtoFromUser;
import ru.practicum.shareit.item.dto.Item;
import ru.practicum.shareit.item.dto.ItemDtoForUser;
import ru.practicum.shareit.item.dto.ItemDtoFromUser;
import ru.practicum.shareit.item.dto.ItemDtoFromUserCreation;
import ru.practicum.shareit.item.exception.ItemValidationException;

import java.time.LocalDateTime;
import java.util.Collection;

public interface ItemService {
    ItemDtoForUser createItem(Long userId, ItemDtoFromUserCreation itemDto) throws ItemValidationException;

    ItemDtoForUser updateItem(Long userId, ItemDtoFromUser itemDto, Long itemId) throws ItemValidationException;

    ItemDtoForUser getItemDtoForUserById(Long userId, Long id);

    Collection<ItemDtoForUser> getItemsOfUser(Long userId, int from, int size);

    Collection<Item> searchItems(String text, int from, int size);

    void deleteItem(Long id);

    Item getItemById(Long itemId);

    CommentDtoForUser addComment(Long userId, CommentDtoFromUser commentDtoFromUser, Long itemId, LocalDateTime moment) throws ItemValidationException;

    void deleteAllItems();

    void deleteAllComments();
}