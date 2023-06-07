package ru.practicum.shareit.item;

import com.querydsl.core.types.dsl.BooleanExpression;
import lombok.AccessLevel;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.PageNumber;
import ru.practicum.shareit.booking.BookingMapper;
import ru.practicum.shareit.booking.BookingRepository;
import ru.practicum.shareit.booking.dto.Booking;
import ru.practicum.shareit.booking.dto.BookingDtoForUser;
import ru.practicum.shareit.booking.model.Status;
import ru.practicum.shareit.item.comment.*;
import ru.practicum.shareit.item.dto.*;
import ru.practicum.shareit.item.exception.ItemNotFoundException;
import ru.practicum.shareit.item.exception.ItemValidationException;
import ru.practicum.shareit.request.ItemRequestRepository;
import ru.practicum.shareit.request.dto.ItemRequest;
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
@FieldDefaults(makeFinal = true, level = AccessLevel.PRIVATE)
public class ItemServiceImpl implements ItemService {
    UserService userService;
    ItemRepository itemRepository;
    BookingRepository bookingRepository;
    CommentRepository commentRepository;
    ItemRequestRepository itemRequestRepository;

    @Autowired
    public ItemServiceImpl(UserServiceImpl userService, ItemRepository itemRepository,
                           BookingRepository bookingRepository, CommentRepository commentRepository, ItemRequestRepository itemRequestRepository) {
        this.userService = userService;
        this.itemRepository = itemRepository;
        this.bookingRepository = bookingRepository;
        this.commentRepository = commentRepository;
        this.itemRequestRepository = itemRequestRepository;
    }

    @Override
    public ItemDtoForUser createItem(Long userId, ItemDtoFromUserCreation itemDto) throws ItemValidationException {
        User user = userService.getUserById(userId);
        ItemRequest itemRequest = validateItemRequest(itemDto.getRequestId());

        Item item = ItemMapper.toItem(itemDto);
        item.setOwner(user);
        item.setRequest(itemRequest);

        Item newItem = itemRepository.save(item);
        log.info(String.format("Новая вещь добавлена в базу: %s c id # %d.", newItem.getName(), newItem.getId()));

        return getItemDtoForUserById(userId, newItem.getId());
    }

    private ItemRequest validateItemRequest(Long requestId) {
        ItemRequest itemRequest = null;

        if (requestId != null) {
            itemRequest = itemRequestRepository.findById(requestId).orElseThrow(() -> new ItemRequestNotFoundException(String.format("Запрос с id #%d не найден", requestId)));
        }
        return itemRequest;
    }

    @Override
    public ItemDtoForUser updateItem(Long userId, ItemDtoFromUser itemDto, Long itemId) {
        User user = userService.getUserById(userId);
        Item item = getItemById(itemId);

        if (!Objects.equals(item.getOwner().getId(), user.getId())) {
            throw new ItemNotFoundException(String.format("Пользователь с id #%d не может редактировать вещь с id #%d " +
                    "(вещь относится к пользователю с id #%d).", user.getId(), item.getId(), item.getOwner().getId()));
        }

        if (itemDto.getName() == null) {
            itemDto.setName(item.getName());
        }

        if (itemDto.getDescription() == null) {
            itemDto.setDescription(item.getDescription());
        }

        if (itemDto.getAvailable() == null) {
            itemDto.setAvailable(item.getAvailable());
        }

        Item updatedItem = ItemMapper.toItem(itemDto);
        updatedItem.setOwner(user);
        updatedItem.setId(item.getId());

        ItemRequest itemRequest = validateItemRequest(itemDto.getRequestId());

        if (itemRequest == null) {
            itemRequest = item.getRequest();
            updatedItem.setRequest(itemRequest);
        } else {
            updatedItem.setRequest(itemRequest);
        }

        Item newItem = itemRepository.save(updatedItem);
        log.info(String.format("Вещь обновлена в базе: %s c id # %d.", newItem.getName(), newItem.getId()));
        return getItemDtoForUserById(userId, newItem.getId());
    }

    @Override
    public ItemDtoForUser getItemDtoForUserById(Long userId, Long id) {
        User user = userService.getUserById(userId);
        Item item = itemRepository.findById(id)
                .orElseThrow(() -> new ItemNotFoundException(String.format("Вещь с id #%d отсутствует в базе.", id)));
        List<Comment> comments = commentRepository.findByItemEquals(item);
        List<CommentDtoForUser> commentsDtoForUser = new ArrayList<>();

        for (Comment data : comments) {
            CommentDtoForUser commentDtoForUser = CommentMapper.toCommentDtoForUser(data);
            commentsDtoForUser.add(commentDtoForUser);
        }

        Long requestId = null;
        ItemRequest itemRequest = null;

        if (item.getRequest() != null) {
            itemRequest = validateItemRequest(item.getRequest().getId());
        }

        if (itemRequest != null) {
            requestId = itemRequest.getId();
        }

        if (Objects.equals(user.getId(), item.getOwner().getId())) {
            LocalDateTime moment = LocalDateTime.now();
            long limit = 1;

            Booking lastBooking = bookingRepository.findLastBooking(item.getId(), moment, Status.REJECTED.toString(), limit);
            Booking nextBooking = bookingRepository.findNextBooking(item.getId(), moment, Status.REJECTED.toString(), limit);

            if ((lastBooking != null) && (nextBooking != null)) {
                BookingDtoForUser lastBookingDtoForUser = BookingMapper.toBookingDtoForUser(lastBooking);
                BookingDtoForUser nextBookingDtoForUser = BookingMapper.toBookingDtoForUser(nextBooking);
                return ItemMapper.toItemDtoForUser(item, requestId, lastBookingDtoForUser, nextBookingDtoForUser, commentsDtoForUser);

            } else if ((lastBooking == null) && (nextBooking == null)) {
                return ItemMapper.toItemDtoForUser(item, requestId, null, null, commentsDtoForUser);

            } else if (lastBooking == null) {
                BookingDtoForUser nextBookingDtoForUser = BookingMapper.toBookingDtoForUser(nextBooking);
                return ItemMapper.toItemDtoForUser(item, requestId, null, nextBookingDtoForUser, commentsDtoForUser);

            } else {
                BookingDtoForUser lastBookingDtoForUser = BookingMapper.toBookingDtoForUser(lastBooking);
                return ItemMapper.toItemDtoForUser(item, requestId, lastBookingDtoForUser, null, commentsDtoForUser);
            }

        } else {
            return ItemMapper.toItemDtoForUser(item, requestId, null, null, commentsDtoForUser);
        }
    }

    @Override
    public Item getItemById(Long id) {
        return itemRepository.findById(id).orElseThrow(() -> new ItemNotFoundException(String.format("Вещь с id #%d отсутствует в базе.", id)));
    }

    @Override
    public CommentDtoForUser addComment(Long userId, CommentDtoFromUser commentDtoFromUser, Long itemId, LocalDateTime creation) throws ItemValidationException {
        LocalDateTime moment = Objects.requireNonNullElseGet(creation, LocalDateTime::now);

        User user = userService.getUserById(userId);
        Item item = getItemById(itemId);
        List<Booking> bookingsOfItemOfUser = bookingRepository.findByBookerEqualsAndItemEqualsAndEndBefore(user, item, moment);

        if (bookingsOfItemOfUser.size() == 0) {
            throw new ItemValidationException(String.format("Пользователь c id # %d не имеет завершенных бронирований для вещи c id # %d.", user.getId(), item.getId()));
        }

        Comment comment = CommentMapper.toComment(commentDtoFromUser, user, item, moment);
        Comment newComment = commentRepository.save(comment);
        log.info(String.format("Новый отзыв с id # %d добавлен в базу: к вещи %s c id # %d.", newComment.getId(),
                newComment.getItem().getName(), newComment.getItem().getId()));

        return CommentMapper.toCommentDtoForUser(newComment);
    }

    @Override
    public Collection<ItemDtoForUser> getItemsOfUser(Long userId, int from, int size) {
        User user = userService.getUserById(userId);
        QItem item = QItem.item;
        List<BooleanExpression> conditions = new ArrayList<>();

        conditions.add(item.owner.id.eq(user.getId()));

        BooleanExpression finalCondition = conditions.stream()
                .reduce(BooleanExpression::and)
                .get();

        Sort sortById = Sort.by(Sort.Direction.ASC, "id");
        PageRequest pageRequest = PageRequest.of(PageNumber.get(from, size), size, sortById);
        Iterable<Item> items = itemRepository.findAll(finalCondition, pageRequest);
        List<ItemDtoForUser> itemsDtoForUser = new ArrayList<>();

        for (Item data : items) {
            ItemDtoForUser itemDtoForUser = getItemDtoForUserById(userId, data.getId());
            itemsDtoForUser.add(itemDtoForUser);
        }
        return itemsDtoForUser;
    }

    @Override
    public Collection<Item> searchItems(String text, int from, int size) {
        List<Item> result = new ArrayList<>();
        String anyText = "%";

        if (text.isBlank()) {
            return result;
        }

        QItem item = QItem.item;
        List<BooleanExpression> conditions = new ArrayList<>();
        String condition = String.format("%s%s%s", anyText, text, anyText);

        conditions.add((item.name.likeIgnoreCase(condition)).or(item.description.likeIgnoreCase(condition)));
        conditions.add(item.available.eq(true));

        BooleanExpression finalCondition = conditions.stream()
                .reduce(BooleanExpression::and)
                .get();

        Sort sortById = Sort.by(Sort.Direction.ASC, "id");
        PageRequest pageRequest = PageRequest.of(PageNumber.get(from, size), size, sortById);
        Iterable<Item> items = itemRepository.findAll(finalCondition, pageRequest);

        for (Item data : items) {
            result.add(data);
        }
        return result;
    }

    @Override
    public void deleteItem(Long id) {
        itemRepository.deleteById(id);
    }

    @Override
    public void deleteAllItems() {
        itemRepository.deleteAll();
    }

    @Override
    public void deleteAllComments() {
        commentRepository.deleteAll();
    }
}