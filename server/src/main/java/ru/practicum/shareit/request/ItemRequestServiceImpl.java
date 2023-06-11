package ru.practicum.shareit.request;

import lombok.AccessLevel;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.PageNumber;
import ru.practicum.shareit.item.ItemRepository;
import ru.practicum.shareit.item.ItemService;
import ru.practicum.shareit.item.dto.Item;
import ru.practicum.shareit.item.dto.ItemDtoForUser;
import ru.practicum.shareit.request.dto.ItemRequest;
import ru.practicum.shareit.request.dto.ItemRequestDtoForUser;
import ru.practicum.shareit.request.dto.ItemRequestDtoFromUser;
import ru.practicum.shareit.request.exception.ItemRequestNotFoundException;
import ru.practicum.shareit.user.UserService;
import ru.practicum.shareit.user.UserServiceImpl;
import ru.practicum.shareit.user.dto.User;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Objects;

@Service
@Slf4j
@Transactional(readOnly = true)
@FieldDefaults(makeFinal = true, level = AccessLevel.PRIVATE)
public class ItemRequestServiceImpl implements ItemRequestService {
    ItemRequestRepository itemRequestRepository;
    ItemRepository itemRepository;
    UserService userService;
    ItemService itemService;

    public ItemRequestServiceImpl(ItemRequestRepository itemRequestRepository, ItemRepository itemRepository, UserServiceImpl userService, ItemService itemService) {
        this.itemRequestRepository = itemRequestRepository;
        this.itemRepository = itemRepository;
        this.userService = userService;
        this.itemService = itemService;
    }

    @Transactional
    @Override
    public ItemRequestDtoForUser createItemRequest(Long userId, ItemRequestDtoFromUser itemRequestDtoFromUser) {
        User user = userService.getUserById(userId);
        LocalDateTime moment = LocalDateTime.now();
        ItemRequest itemRequest = ItemRequestMapper.toItemRequest(itemRequestDtoFromUser, user, moment);
        ItemRequest newItemRequest = itemRequestRepository.save(itemRequest);
        log.info(String.format("Новый запрос добавлен в базу: id # %d.", newItemRequest.getId()));
        return getItemRequestDtoForUser(user, newItemRequest);
    }

    @Override
    public ItemRequestDtoForUser getItemRequestById(Long userId, Long id) {
        User user = userService.getUserById(userId);
        ItemRequest itemRequest = itemRequestRepository.findById(id).orElseThrow(() -> new ItemRequestNotFoundException(String.format("Запрос с id # %d отсутствует в базе.", id)));
        return getItemRequestDtoForUser(user, itemRequest);
    }

    @Override
    public Collection<ItemRequestDtoForUser> getOwnItemRequests(Long userId) {
        User user = userService.getUserById(userId);
        List<ItemRequest> itemRequests = itemRequestRepository.findByRequesterEquals(user);
        List<ItemRequestDtoForUser> itemRequestDtoForUserList = new ArrayList<>();

        for (ItemRequest data : itemRequests) {
            ItemRequestDtoForUser itemRequestDtoForUser = getItemRequestDtoForUser(user, data);
            itemRequestDtoForUserList.add(itemRequestDtoForUser);
        }
        return itemRequestDtoForUserList;
    }

    @Override
    public Collection<ItemRequestDtoForUser> getAllItemRequests(Long userId, Integer from, Integer size) {
        User user = userService.getUserById(userId);

        Sort sortByDate = Sort.by(Sort.Direction.ASC, "created");
        Pageable page = PageRequest.of(PageNumber.get(from, size), size, sortByDate);

        Page<ItemRequest> requestPage = itemRequestRepository.findAll(page);
        List<ItemRequest> items = requestPage.getContent();
        List<ItemRequestDtoForUser> itemRequestDtoForUserList = new ArrayList<>();
        ItemRequestDtoForUser itemRequestDtoForUser;

        for (ItemRequest data : items) {
            if (!Objects.equals(data.getRequester().getId(), user.getId())) {
                itemRequestDtoForUser = getItemRequestById(userId, data.getId());
                itemRequestDtoForUserList.add(itemRequestDtoForUser);
            }
        }
        return itemRequestDtoForUserList;
    }

    @Transactional
    @Override
    public void deleteAll() {
        itemRequestRepository.deleteAll();
    }

    private ItemRequestDtoForUser getItemRequestDtoForUser(User user, ItemRequest itemRequest) {
        ItemRequestDtoForUser itemRequestDtoForUser = ItemRequestMapper.toItemRequestDtoForUser(itemRequest);
        List<Item> items = itemRepository.findByRequestId(itemRequest.getId());
        List<ItemDtoForUser> itemDtoForUserList = new ArrayList<>();

        for (Item data : items) {
            ItemDtoForUser itemDtoForUser = itemService.getItemDtoForUserById(user.getId(), data.getId());
            itemDtoForUserList.add(itemDtoForUser);
        }

        itemRequestDtoForUser.setItems(itemDtoForUserList);
        return itemRequestDtoForUser;
    }
}