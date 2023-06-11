package ru.practicum.shareit.request;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import ru.practicum.shareit.TestHelper;
import ru.practicum.shareit.booking.BookingService;
import ru.practicum.shareit.item.ItemService;
import ru.practicum.shareit.request.dto.ItemRequest;
import ru.practicum.shareit.request.dto.ItemRequestDtoForUser;
import ru.practicum.shareit.request.dto.ItemRequestDtoFromUser;
import ru.practicum.shareit.request.exception.ItemRequestNotFoundException;
import ru.practicum.shareit.user.UserService;
import ru.practicum.shareit.user.dto.User;

import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.persistence.TypedQuery;
import java.util.ArrayList;
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
public class ItemRequestServiceImplTest {
    ItemService itemService;
    UserService userService;
    BookingService bookingService;
    ItemRequestService requestService;
    EntityManager em;
    User user = TestHelper.getUserWithoutId1();
    User secondUser = TestHelper.getUserWithoutId2();
    String description = TestHelper.getShoeBrush();
    String description2 = TestHelper.getToothBrush();

    @AfterEach
    void deleteAll() {
        requestService.deleteAll();
        bookingService.deleteAll();
        itemService.deleteAllItems();
        userService.deleteAll();
    }

    @Test
    void createItemRequestTest() throws Exception {
        User newUser = userService.createUser(user);

        ItemRequestDtoFromUser request = new ItemRequestDtoFromUser();
        request.setDescription(description);

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
    void getOwnItemRequestsTest() throws Exception {
        User newUser = userService.createUser(user);

        ItemRequestDtoFromUser request = new ItemRequestDtoFromUser();
        request.setDescription(description);

        ItemRequestDtoFromUser request2 = new ItemRequestDtoFromUser();
        request2.setDescription(description2);

        ItemRequestDtoForUser checkRequest = requestService.createItemRequest(newUser.getId(), request);
        ItemRequestDtoForUser checkRequest2 = requestService.createItemRequest(newUser.getId(), request2);

        List<ItemRequestDtoForUser> requests = new ArrayList<>();
        requests.add(checkRequest);
        requests.add(checkRequest2);

        Collection<ItemRequestDtoForUser> targetRequests = requestService.getOwnItemRequests(newUser.getId());

        assertThat(targetRequests, hasSize(requests.size()));

        for (ItemRequestDtoForUser data : requests) {
            assertThat(targetRequests, hasItem(allOf(
                    hasProperty("id", notNullValue()),
                    hasProperty("id", equalTo(data.getId())),
                    hasProperty("description", equalTo(data.getDescription()))
            )));
        }
    }

    @Test
    void deleteAllTest() throws Exception {
        User newUser = userService.createUser(user);

        ItemRequestDtoFromUser request = new ItemRequestDtoFromUser();
        request.setDescription(description);

        ItemRequestDtoForUser checkRequest = requestService.createItemRequest(newUser.getId(), request);

        requestService.deleteAll();

        final NoResultException exception = assertThrows(
                NoResultException.class,
                () -> {
                    TypedQuery<ItemRequest> query = em.createQuery("Select i from ItemRequest i where i.id = :id", ItemRequest.class);
                    ItemRequest newRequest = query
                            .setParameter("id", checkRequest.getId())
                            .getSingleResult();
                });
        assertEquals("No entity found for query", exception.getMessage());
    }

    @Test
    void getItemRequestByIdBasicTest() throws Exception {
        User newUser = userService.createUser(user);

        ItemRequestDtoFromUser request = new ItemRequestDtoFromUser();
        request.setDescription(description);

        ItemRequestDtoForUser checkRequest = requestService.createItemRequest(newUser.getId(), request);
        ItemRequestDtoForUser newCheckRequest = requestService.getItemRequestById(newUser.getId(), checkRequest.getId());

        TypedQuery<ItemRequest> query = em.createQuery("Select i from ItemRequest i where i.id = :id", ItemRequest.class);
        ItemRequest newRequest = query
                .setParameter("id", checkRequest.getId())
                .getSingleResult();

        assertThat(newRequest.getId(), notNullValue());
        assertThat(newRequest.getId(), equalTo(newCheckRequest.getId()));
        assertThat(newRequest.getDescription(), equalTo(newCheckRequest.getDescription()));
    }

    @Test
    void getItemRequestByIdWithIncorrectIdTest() throws Exception {
        User newUser = userService.createUser(user);
        Long id = 999L;

        final ItemRequestNotFoundException exception = assertThrows(
                ItemRequestNotFoundException.class,
                () -> requestService.getItemRequestById(newUser.getId(), id));
        assertEquals(String.format("Запрос с id # %d отсутствует в базе.", id), exception.getMessage());
    }

    @Test
    void getAllItemRequestsTest() throws Exception {
        User newUser = userService.createUser(user);
        User newSecondUser = userService.createUser(secondUser);

        ItemRequestDtoFromUser request = new ItemRequestDtoFromUser();
        request.setDescription(description);

        ItemRequestDtoFromUser request2 = new ItemRequestDtoFromUser();
        request2.setDescription(description2);

        ItemRequestDtoForUser checkRequest = requestService.createItemRequest(newUser.getId(), request);
        ItemRequestDtoForUser checkRequest2 = requestService.createItemRequest(newUser.getId(), request2);

        List<ItemRequestDtoForUser> requests = new ArrayList<>();
        requests.add(checkRequest);
        requests.add(checkRequest2);

        Collection<ItemRequestDtoForUser> targetRequests = requestService.getAllItemRequests(newSecondUser.getId(), 0, 20);
        assertThat(targetRequests, hasSize(requests.size()));

        for (ItemRequestDtoForUser data : requests) {
            assertThat(targetRequests, hasItem(allOf(
                    hasProperty("id", notNullValue()),
                    hasProperty("id", equalTo(data.getId())),
                    hasProperty("description", equalTo(data.getDescription()))
            )));
        }
    }

    @Test
    void getAllItemRequestsTestWithPagination() throws Exception {
        User newUser = userService.createUser(user);
        User newSecondUser = userService.createUser(secondUser);

        ItemRequestDtoFromUser request = new ItemRequestDtoFromUser();
        request.setDescription(description);

        ItemRequestDtoFromUser request2 = new ItemRequestDtoFromUser();
        request2.setDescription(description2);

        ItemRequestDtoFromUser request3 = new ItemRequestDtoFromUser();
        request3.setDescription(TestHelper.getDrill());

        requestService.createItemRequest(newUser.getId(), request);
        requestService.createItemRequest(newUser.getId(), request2);
        requestService.createItemRequest(newUser.getId(), request3);

        Collection<ItemRequestDtoForUser> targetRequests = requestService.getAllItemRequests(newSecondUser.getId(), 0, 1);
        assertThat(targetRequests, hasSize(1));

        targetRequests = requestService.getAllItemRequests(newSecondUser.getId(), 1, 1);
        assertThat(targetRequests, hasSize(1));

        targetRequests = requestService.getAllItemRequests(newSecondUser.getId(), 2, 1);
        assertThat(targetRequests, hasSize(1));
    }
}