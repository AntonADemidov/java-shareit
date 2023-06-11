package ru.practicum.shareit.item;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import ru.practicum.shareit.TestHelper;
import ru.practicum.shareit.booking.BookingMapper;
import ru.practicum.shareit.booking.BookingService;
import ru.practicum.shareit.booking.dto.Booking;
import ru.practicum.shareit.booking.dto.BookingDtoForUser;
import ru.practicum.shareit.booking.dto.BookingDtoFromUser;
import ru.practicum.shareit.item.comment.Comment;
import ru.practicum.shareit.item.comment.CommentDtoForUser;
import ru.practicum.shareit.item.comment.CommentDtoFromUser;
import ru.practicum.shareit.item.dto.Item;
import ru.practicum.shareit.item.dto.ItemDtoForUser;
import ru.practicum.shareit.item.dto.ItemDtoFromUser;
import ru.practicum.shareit.item.dto.ItemDtoFromUserCreation;
import ru.practicum.shareit.item.exception.ItemNotFoundException;
import ru.practicum.shareit.item.exception.ItemValidationException;
import ru.practicum.shareit.request.ItemRequestMapper;
import ru.practicum.shareit.request.ItemRequestService;
import ru.practicum.shareit.request.dto.ItemRequest;
import ru.practicum.shareit.request.dto.ItemRequestDtoForUser;
import ru.practicum.shareit.request.dto.ItemRequestDtoFromUser;
import ru.practicum.shareit.user.UserService;
import ru.practicum.shareit.user.dto.User;

import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.persistence.TypedQuery;
import java.time.LocalDateTime;
import java.util.Collection;
import java.util.List;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

@SpringBootTest(
        properties = "db.name=test",
        webEnvironment = SpringBootTest.WebEnvironment.NONE)
@RequiredArgsConstructor(onConstructor_ = @Autowired)
@FieldDefaults(makeFinal = true, level = AccessLevel.PRIVATE)
public class ItemServiceImplTest {
    ItemService itemService;
    UserService userService;
    BookingService bookingService;
    ItemRequestService requestService;
    EntityManager em;
    User user = TestHelper.getUserWithoutId1();
    ItemDtoFromUserCreation item = new ItemDtoFromUserCreation("Дрель", "Простая дрель", true, null);
    ItemDtoFromUser updatedItem = new ItemDtoFromUser("Дрель+", "Аккумуляторная дрель", false, null);
    ItemDtoFromUserCreation updatedItemCreation = new ItemDtoFromUserCreation("Дрель+", "Аккумуляторная дрель", true, null);

    @AfterEach
    void deleteAll() {
        itemService.deleteAllComments();
        bookingService.deleteAll();
        itemService.deleteAllItems();
        requestService.deleteAll();
        userService.deleteAll();
    }

    @Test
    void createItemSuccessfulTest() throws Exception {
        User newUser = userService.createUser(user);
        ItemDtoForUser itemDto = itemService.createItem(newUser.getId(), item);

        TypedQuery<Item> query = em.createQuery("Select i from Item i where i.name = :name", Item.class);
        Item newItem = query
                .setParameter("name", item.getName())
                .getSingleResult();

        assertThat(newItem.getId(), notNullValue());
        assertThat(newItem.getId(), equalTo(itemDto.getId()));
        assertThat(newItem.getName(), equalTo(item.getName()));
        assertThat(newItem.getDescription(), equalTo(item.getDescription()));
        assertThat(newItem.getAvailable(), equalTo(item.getAvailable()));
        assertThat(newItem.getOwner(), equalTo(newUser));
        assertThat(newItem.getRequest(), equalTo(null));
    }

    @Test
    void getItemByIdWithCorrectIdTest() throws Exception {
        User newUser = userService.createUser(user);
        ItemDtoForUser itemDto = itemService.createItem(newUser.getId(), item);

        Item savedItem = itemService.getItemById(itemDto.getId());

        TypedQuery<Item> query = em.createQuery("Select i from Item i where i.id = :id", Item.class);
        Item newItem = query
                .setParameter("id", itemDto.getId())
                .getSingleResult();

        assertThat(newItem.getId(), notNullValue());
        assertThat(newItem.getId(), equalTo(savedItem.getId()));
        assertThat(newItem.getName(), equalTo(savedItem.getName()));
        assertThat(newItem.getDescription(), equalTo(savedItem.getDescription()));
        assertThat(newItem.getAvailable(), equalTo(savedItem.getAvailable()));
        assertThat(newItem.getOwner(), equalTo(newUser));
        assertThat(newItem.getRequest(), equalTo(null));
    }

    @Test
    void getItemByIdWithIncorrectIdTest() {
        Long id = 999L;

        final ItemNotFoundException exception = assertThrows(
                ItemNotFoundException.class,
                () -> itemService.getItemById(id));
        assertEquals(String.format("Вещь с id #%d отсутствует в базе.", id), exception.getMessage());
    }

    @Test
    void updateItemWIthAllDataTest() throws Exception {
        User newUser = userService.createUser(user);
        ItemDtoForUser itemDto = itemService.createItem(newUser.getId(), item);

        itemService.updateItem(newUser.getId(), updatedItem, itemDto.getId());

        TypedQuery<Item> query = em.createQuery("Select i from Item i where i.name = :name", Item.class);
        Item newItem = query
                .setParameter("name", updatedItem.getName())
                .getSingleResult();

        assertThat(newItem.getId(), notNullValue());
        assertThat(newItem.getId(), equalTo(itemDto.getId()));
        assertThat(newItem.getName(), equalTo(updatedItem.getName()));
        assertThat(newItem.getDescription(), equalTo(updatedItem.getDescription()));
        assertThat(newItem.getAvailable(), equalTo(updatedItem.getAvailable()));
        assertThat(newItem.getOwner(), equalTo(newUser));
        assertThat(newItem.getRequest(), equalTo(null));
    }

    @Test
    void updateItemWithoutNameTest() throws Exception {
        User newUser = userService.createUser(user);
        ItemDtoForUser itemDto = itemService.createItem(newUser.getId(), item);

        updatedItem.setName(null);
        itemService.updateItem(newUser.getId(), updatedItem, itemDto.getId());

        TypedQuery<Item> query = em.createQuery("Select i from Item i where i.name = :name", Item.class);
        Item newItem = query
                .setParameter("name", updatedItem.getName())
                .getSingleResult();

        assertThat(newItem.getId(), notNullValue());
        assertThat(newItem.getId(), equalTo(itemDto.getId()));
        assertThat(newItem.getName(), equalTo(itemDto.getName()));
        assertThat(newItem.getDescription(), equalTo(updatedItem.getDescription()));
        assertThat(newItem.getAvailable(), equalTo(updatedItem.getAvailable()));
        assertThat(newItem.getOwner(), equalTo(newUser));
        assertThat(newItem.getRequest(), equalTo(null));
    }

    @Test
    void updateItemWithoutDescriptionTest() throws Exception {
        User newUser = userService.createUser(user);
        ItemDtoForUser itemDto = itemService.createItem(newUser.getId(), item);

        updatedItem.setDescription(null);
        itemService.updateItem(newUser.getId(), updatedItem, itemDto.getId());

        TypedQuery<Item> query = em.createQuery("Select i from Item i where i.name = :name", Item.class);
        Item newItem = query
                .setParameter("name", updatedItem.getName())
                .getSingleResult();

        assertThat(newItem.getId(), notNullValue());
        assertThat(newItem.getId(), equalTo(itemDto.getId()));
        assertThat(newItem.getName(), equalTo(updatedItem.getName()));
        assertThat(newItem.getDescription(), equalTo(itemDto.getDescription()));
        assertThat(newItem.getAvailable(), equalTo(updatedItem.getAvailable()));
        assertThat(newItem.getOwner(), equalTo(newUser));
        assertThat(newItem.getRequest(), equalTo(null));
    }

    @Test
    void updateItemWithoutAvailableTest() throws Exception {
        User newUser = userService.createUser(user);
        ItemDtoForUser itemDto = itemService.createItem(newUser.getId(), item);

        updatedItem.setAvailable(null);
        itemService.updateItem(newUser.getId(), updatedItem, itemDto.getId());

        TypedQuery<Item> query = em.createQuery("Select i from Item i where i.name = :name", Item.class);
        Item newItem = query
                .setParameter("name", updatedItem.getName())
                .getSingleResult();

        assertThat(newItem.getId(), notNullValue());
        assertThat(newItem.getId(), equalTo(itemDto.getId()));
        assertThat(newItem.getName(), equalTo(updatedItem.getName()));
        assertThat(newItem.getDescription(), equalTo(updatedItem.getDescription()));
        assertThat(newItem.getAvailable(), equalTo(itemDto.getAvailable()));
        assertThat(newItem.getOwner(), equalTo(newUser));
        assertThat(newItem.getRequest(), equalTo(null));
    }

    @Test
    void updateItemByNonOwnerTest() throws Exception {
        User newUser = userService.createUser(user);
        User secondUser = userService.createUser(new User("user2", "user2@user.com"));
        ItemDtoForUser itemDto = itemService.createItem(newUser.getId(), item);

        final ItemNotFoundException exception = assertThrows(
                ItemNotFoundException.class,
                () -> itemService.updateItem(secondUser.getId(), updatedItem, itemDto.getId()));
        assertEquals(String.format("Пользователь с id #%d не может редактировать вещь с id #%d " +
                "(вещь относится к пользователю с id #%d).", secondUser.getId(), itemDto.getId(), newUser.getId()), exception.getMessage());
    }

    @Test
    void getItemDtoForUserByIdBasicTest() throws Exception {
        User newUser = userService.createUser(user);
        ItemDtoForUser itemDto = itemService.createItem(newUser.getId(), item);

        ItemDtoForUser newItemDto = itemService.getItemDtoForUserById(newUser.getId(), itemDto.getId());

        TypedQuery<Item> query = em.createQuery("Select i from Item i where i.id = :id", Item.class);
        Item newItem = query
                .setParameter("id", itemDto.getId())
                .getSingleResult();

        assertThat(newItem.getId(), notNullValue());
        assertThat(newItem.getId(), equalTo(newItemDto.getId()));
        assertThat(newItem.getName(), equalTo(newItemDto.getName()));
        assertThat(newItem.getDescription(), equalTo(newItemDto.getDescription()));
        assertThat(newItem.getAvailable(), equalTo(newItemDto.getAvailable()));
        assertThat(newItem.getOwner(), equalTo(newUser));
        assertThat(newItem.getRequest(), equalTo(null));
    }

    @Test
    void getItemDtoForUserByIdWithItemRequestIdTest() throws Exception {
        User newUser = userService.createUser(user);

        ItemRequestDtoFromUser requestDto = new ItemRequestDtoFromUser();
        requestDto.setDescription("Хотел бы воспользоваться щёткой для обуви");
        ItemRequestDtoForUser checkRequest = requestService.createItemRequest(newUser.getId(), requestDto);
        ItemRequest request = ItemRequestMapper.toItemRequest(checkRequest, newUser);

        ItemDtoFromUserCreation item = new ItemDtoFromUserCreation("Дрель", "Простая дрель", true, checkRequest.getId());

        ItemDtoForUser itemDto = itemService.createItem(newUser.getId(), item);

        ItemDtoForUser newItemDto = itemService.getItemDtoForUserById(newUser.getId(), itemDto.getId());

        TypedQuery<Item> query = em.createQuery("Select i from Item i where i.id = :id", Item.class);
        Item newItem = query
                .setParameter("id", itemDto.getId())
                .getSingleResult();

        assertThat(newItem.getId(), notNullValue());
        assertThat(newItem.getId(), equalTo(newItemDto.getId()));
        assertThat(newItem.getName(), equalTo(newItemDto.getName()));
        assertThat(newItem.getDescription(), equalTo(newItemDto.getDescription()));
        assertThat(newItem.getAvailable(), equalTo(newItemDto.getAvailable()));
        assertThat(newItem.getOwner(), equalTo(newUser));
        assertThat(newItem.getRequest().getId(), equalTo(request.getId()));
    }

    @Test
    void createItemRequestTest() throws Exception {
        User newUser = userService.createUser(user);

        ItemRequestDtoFromUser request = new ItemRequestDtoFromUser();
        request.setDescription("Хотел бы воспользоваться щёткой для обуви");

        ItemRequestDtoForUser checkRequest = requestService.createItemRequest(newUser.getId(), request);

        TypedQuery<ItemRequest> query = em.createQuery("Select i from ItemRequest i where i.id = :id", ItemRequest.class);
        ItemRequest newRequest = query
                .setParameter("id", checkRequest.getId())
                .getSingleResult();

        assertThat(newRequest.getId(), notNullValue());
        assertThat(newRequest.getId(), equalTo(checkRequest.getId()));
        assertThat(newRequest.getDescription(), equalTo(checkRequest.getDescription()));
    }

    @Test
    void getItemDtoForUserByIdWithIncorrectIdTest() throws Exception {
        Long id = 999L;
        User newUser = userService.createUser(user);
        itemService.createItem(newUser.getId(), item);

        final ItemNotFoundException exception = assertThrows(
                ItemNotFoundException.class,
                () -> itemService.getItemDtoForUserById(newUser.getId(), id));
        assertEquals(String.format("Вещь с id #%d отсутствует в базе.", id), exception.getMessage());
    }

    @Test
    void deleteItemTest() throws Exception {
        User newUser = userService.createUser(user);
        ItemDtoForUser itemDto = itemService.createItem(newUser.getId(), item);

        itemService.deleteItem(itemDto.getId());

        final NoResultException exception = assertThrows(
                NoResultException.class,
                () -> {
                    TypedQuery<Item> query = em.createQuery("Select i from Item i where i.id = :id", Item.class);
                    query
                            .setParameter("id", itemDto.getId())
                            .getSingleResult();
                });
        assertEquals("No entity found for query", exception.getMessage());
    }

    @Test
    void deleteAllTest() throws Exception {
        User newUser = userService.createUser(user);
        ItemDtoForUser itemDto = itemService.createItem(newUser.getId(), item);

        itemService.deleteAllItems();

        final NoResultException exception = assertThrows(
                NoResultException.class,
                () -> {
                    TypedQuery<Item> query = em.createQuery("Select i from Item i where i.id = :id", Item.class);
                    query
                            .setParameter("id", itemDto.getId())
                            .getSingleResult();
                });
        assertEquals("No entity found for query", exception.getMessage());
    }

    @Test
    void findItemsOfUserTest() throws Exception {
        User newUser = userService.createUser(user);
        List<ItemDtoFromUserCreation> sourceItems = List.of(
                item,
                updatedItemCreation
        );

        for (ItemDtoFromUserCreation item : sourceItems) {
            itemService.createItem(newUser.getId(), item);
        }

        Collection<ItemDtoForUser> targetItems = itemService.getItemsOfUser(newUser.getId(), 0, 20);

        assertThat(targetItems, hasSize(sourceItems.size()));

        for (ItemDtoFromUserCreation item : sourceItems) {
            assertThat(targetItems, hasItem(allOf(
                    hasProperty("id", notNullValue()),
                    hasProperty("name", equalTo(item.getName())),
                    hasProperty("description", equalTo(item.getDescription())),
                    hasProperty("available", equalTo(item.getAvailable())),
                    hasProperty("requestId", equalTo(item.getRequestId()))
            )));
        }
    }

    @Test
    void findItemsOfUserWithPaginationTest() throws Exception {
        User newUser = userService.createUser(user);
        List<ItemDtoFromUserCreation> sourceItems = List.of(
                item,
                updatedItemCreation,
                new ItemDtoFromUserCreation("Дрель++", "Аккумуляторная дрель++", true, null)
        );

        for (ItemDtoFromUserCreation item : sourceItems) {
            itemService.createItem(newUser.getId(), item);
        }

        Collection<ItemDtoForUser> targetItems = itemService.getItemsOfUser(newUser.getId(), 0, 1);
        assertThat(targetItems, hasSize(1));

        targetItems = itemService.getItemsOfUser(newUser.getId(), 1, 1);
        assertThat(targetItems, hasSize(1));

        targetItems = itemService.getItemsOfUser(newUser.getId(), 2, 1);
        assertThat(targetItems, hasSize(1));

        targetItems = itemService.getItemsOfUser(newUser.getId(), 3, 1);
        assertThat(targetItems, hasSize(0));

        targetItems = itemService.getItemsOfUser(newUser.getId(), 0, 2);
        assertThat(targetItems, hasSize(2));

        targetItems = itemService.getItemsOfUser(newUser.getId(), 0, 3);
        assertThat(targetItems, hasSize(3));
    }

    @Test
    void searchItemsTest() throws Exception {
        User newUser = userService.createUser(user);
        updatedItem.setAvailable(true);

        List<ItemDtoFromUserCreation> sourceItems = List.of(
                item,
                updatedItemCreation
        );

        for (ItemDtoFromUserCreation item : sourceItems) {
            itemService.createItem(newUser.getId(), item);
        }

        Collection<Item> targetItems = itemService.searchItems("ДрЕлЬ", 0, 20);

        assertThat(targetItems, hasSize(sourceItems.size()));

        for (ItemDtoFromUserCreation item : sourceItems) {
            assertThat(targetItems, hasItem(allOf(
                    hasProperty("id", notNullValue()),
                    hasProperty("name", equalTo(item.getName())),
                    hasProperty("description", equalTo(item.getDescription())),
                    hasProperty("available", equalTo(item.getAvailable())),
                    hasProperty("request", equalTo(item.getRequestId()))
            )));
        }

        targetItems = itemService.searchItems("", 0, 20);
        assertThat(targetItems, hasSize(0));
    }

    @Test
    void addCommentByUserWithBookingsTest() throws Exception {
        User newUser = userService.createUser(user);
        User secondUser = userService.createUser(new User("Ivan", "ivan@email"));

        ItemDtoForUser itemDto = itemService.createItem(newUser.getId(), item);
        Item savedItem = itemService.getItemById(itemDto.getId());

        BookingDtoFromUser bookingDto = new BookingDtoFromUser(savedItem.getId(), LocalDateTime.now().plusDays(1), LocalDateTime.now().plusDays(2), null, null, null);
        Booking booking = bookingService.createBooking(secondUser.getId(), bookingDto);
        bookingService.updateBookingStatus(newUser.getId(), booking.getId(), true);

        CommentDtoFromUser comment = new CommentDtoFromUser();
        comment.setText("Add comment from user1");
        CommentDtoForUser commentFrom = itemService.addComment(secondUser.getId(), comment, itemDto.getId(), LocalDateTime.now().plusDays(3));

        TypedQuery<Comment> query = em.createQuery("Select c from Comment c where c.text = :text", Comment.class);
        Comment newComment = query
                .setParameter("text", comment.getText())
                .getSingleResult();

        assertThat(commentFrom.getId(), notNullValue());
        assertThat(newComment.getId(), notNullValue());
        assertThat(newComment.getId(), equalTo(commentFrom.getId()));
        assertThat(newComment.getText(), equalTo(commentFrom.getText()));
        assertThat(newComment.getAuthor().getName(), equalTo(commentFrom.getAuthorName()));
    }

    @Test
    void getItemDtoForUserByIdWithNextBookingTest() throws Exception {
        User newUser = userService.createUser(user);
        User secondUser = userService.createUser(new User("Ivan", "ivan@email.ru"));

        ItemDtoForUser itemDto = itemService.createItem(newUser.getId(), item);
        Item savedItem = itemService.getItemById(itemDto.getId());

        BookingDtoFromUser bookingDto = new BookingDtoFromUser(savedItem.getId(), LocalDateTime.now().plusDays(1), LocalDateTime.now().plusDays(2), null, null, null);
        Booking booking = bookingService.createBooking(secondUser.getId(), bookingDto);
        BookingDtoForUser bookingDtoFinal = BookingMapper.toBookingDtoForUser(booking);

        ItemDtoForUser newItemDto = itemService.getItemDtoForUserById(newUser.getId(), itemDto.getId());

        TypedQuery<Item> query = em.createQuery("Select i from Item i where i.id = :id", Item.class);
        Item newItem = query
                .setParameter("id", itemDto.getId())
                .getSingleResult();

        assertThat(newItem.getId(), notNullValue());
        assertThat(newItem.getId(), equalTo(newItemDto.getId()));
        assertThat(newItem.getName(), equalTo(newItemDto.getName()));
        assertThat(newItem.getDescription(), equalTo(newItemDto.getDescription()));
        assertThat(newItem.getAvailable(), equalTo(newItemDto.getAvailable()));
        assertThat(newItem.getOwner(), equalTo(newUser));
        assertThat(newItem.getRequest(), equalTo(null));
        assertThat(bookingDtoFinal, equalTo(newItemDto.getNextBooking()));
    }

    @Test
    void addCommentByUserWithoutBookingsTest() throws Exception {
        User newUser = userService.createUser(user);
        User secondUser = userService.createUser(new User("Ivan", "ivan@email.ru"));
        User thirdUser = userService.createUser(new User("Oleg", "oleg@email.ru"));

        ItemDtoForUser itemDto = itemService.createItem(newUser.getId(), item);
        Item savedItem = itemService.getItemById(itemDto.getId());

        BookingDtoFromUser bookingDto = new BookingDtoFromUser(savedItem.getId(), LocalDateTime.now().plusDays(1), LocalDateTime.now().plusDays(2), null, null, null);
        Booking booking = bookingService.createBooking(secondUser.getId(), bookingDto);
        bookingService.updateBookingStatus(newUser.getId(), booking.getId(), true);

        CommentDtoFromUser comment = new CommentDtoFromUser();
        comment.setText("Add comment from user1");

        final ItemValidationException exception = assertThrows(
                ItemValidationException.class,
                () -> itemService.addComment(thirdUser.getId(), comment, itemDto.getId(), LocalDateTime.now().plusDays(3)));
        assertEquals(String.format("Пользователь c id # %d не имеет завершенных бронирований для вещи c id # %d.", thirdUser.getId(), itemDto.getId()), exception.getMessage());
    }
}