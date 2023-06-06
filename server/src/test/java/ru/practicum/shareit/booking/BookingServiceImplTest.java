package ru.practicum.shareit.booking;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import ru.practicum.shareit.TestHelper;
import ru.practicum.shareit.booking.dto.Booking;
import ru.practicum.shareit.booking.dto.BookingDtoFromUser;
import ru.practicum.shareit.booking.exception.BookingErrorResponse;
import ru.practicum.shareit.booking.exception.BookingNotFoundException;
import ru.practicum.shareit.booking.exception.BookingValidationException;
import ru.practicum.shareit.booking.model.GetBookingRequest;
import ru.practicum.shareit.booking.model.State;
import ru.practicum.shareit.booking.model.Status;
import ru.practicum.shareit.item.ItemService;
import ru.practicum.shareit.item.dto.ItemDtoForUser;
import ru.practicum.shareit.item.dto.ItemDtoFromUserCreation;
import ru.practicum.shareit.user.UserService;
import ru.practicum.shareit.user.dto.User;

import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.persistence.TypedQuery;
import java.time.LocalDateTime;
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
public class BookingServiceImplTest {
    ItemService itemService;
    UserService userService;
    BookingService bookingService;
    EntityManager em;
    User user = TestHelper.getUserWithoutId1();
    User secondUser = TestHelper.getUserWithoutId2();
    ItemDtoFromUserCreation item = new ItemDtoFromUserCreation("Дрель", "Простая дрель", true, null);
    ItemDtoFromUserCreation item2 = new ItemDtoFromUserCreation("Дрель+", "Аккумуляторная дрель", true, null);

    @AfterEach
    void deleteAll() {
        bookingService.deleteAll();
        itemService.deleteAllItems();
        userService.deleteAll();
    }

    @Test
    void createBookingSuccessfulTest() throws Exception {
        User newUser = userService.createUser(user);
        User newSecondUser = userService.createUser(secondUser);
        ItemDtoForUser itemDto = itemService.createItem(newUser.getId(), item);
        BookingDtoFromUser bookingDto = new BookingDtoFromUser(itemDto.getId(), LocalDateTime.now().plusDays(1), LocalDateTime.now().plusDays(2), null, null, null);
        Booking booking = bookingService.createBooking(newSecondUser.getId(), bookingDto);

        TypedQuery<Booking> query = em.createQuery("Select b from Booking b where b.id = :id", Booking.class);
        Booking newBooking = query
                .setParameter("id", booking.getId())
                .getSingleResult();

        assertThat(newBooking.getId(), notNullValue());
        assertThat(newBooking.getId(), equalTo(booking.getId()));
        assertThat(newBooking.getItem(), equalTo(booking.getItem()));
        assertThat(newBooking.getBooker(), equalTo(booking.getBooker()));
        assertThat(newBooking.getStatus(), equalTo(booking.getStatus()));
    }

    @Test
    void createBookingOfUnavailableItemTest() throws Exception {
        User newUser = userService.createUser(user);
        User newSecondUser = userService.createUser(secondUser);

        final ItemDtoFromUserCreation itemNew = new ItemDtoFromUserCreation("Дрель", "Простая дрель", false, null);
        ItemDtoForUser itemDto = itemService.createItem(newUser.getId(), itemNew);
        BookingDtoFromUser bookingDto = new BookingDtoFromUser(itemDto.getId(), LocalDateTime.now().plusDays(1), LocalDateTime.now().plusDays(2), null, null, null);

        final BookingValidationException exception = assertThrows(
                BookingValidationException.class,
                () -> bookingService.createBooking(newSecondUser.getId(), bookingDto));
        assertEquals("Вещь недоступна для бронирования: значение available равно false.", exception.getMessage());

        BookingErrorResponse bookingErrorResponse = new BookingErrorResponse(exception.getMessage());
        assertEquals(bookingErrorResponse.getError(), exception.getMessage());
    }

    @Test
    void createBookingWithIncorrectTimeParametersTest() throws Exception {
        User newUser = userService.createUser(user);
        User newSecondUser = userService.createUser(secondUser);
        LocalDateTime moment = LocalDateTime.now().plusDays(1);
        LocalDateTime start = moment;
        LocalDateTime end = moment;

        ItemDtoForUser itemDto = itemService.createItem(newUser.getId(), item);
        BookingDtoFromUser bookingDto = new BookingDtoFromUser(itemDto.getId(), start, end, null, null, null);

        final BookingValidationException exception2 = assertThrows(
                BookingValidationException.class,
                () -> bookingService.createBooking(newSecondUser.getId(), bookingDto));
        assertEquals("Несоответствие дат в запросе: значение END не может быть ранее или равно значению START.", exception2.getMessage());
    }

    @Test
    void createBookingOfOwnItemTest() throws Exception {
        User newUser = userService.createUser(user);
        ItemDtoForUser itemDto = itemService.createItem(newUser.getId(), item);
        BookingDtoFromUser bookingDto = new BookingDtoFromUser(itemDto.getId(), LocalDateTime.now().plusDays(1), LocalDateTime.now().plusDays(2), null, null, null);

        final BookingNotFoundException exception = assertThrows(
                BookingNotFoundException.class,
                () -> bookingService.createBooking(newUser.getId(), bookingDto));
        assertEquals("Недопустимое действие: нельзя бронировать собственную вещь.", exception.getMessage());
    }

    @Test
    void updateBookingStatusTest() throws Exception {
        User newUser = userService.createUser(user);
        User newSecondUser = userService.createUser(secondUser);
        ItemDtoForUser itemDto = itemService.createItem(newUser.getId(), item);
        BookingDtoFromUser bookingDto = new BookingDtoFromUser(itemDto.getId(), LocalDateTime.now().plusDays(1), LocalDateTime.now().plusDays(2), null, null, null);
        Booking booking = bookingService.createBooking(newSecondUser.getId(), bookingDto);

        final BookingNotFoundException exception = assertThrows(
                BookingNotFoundException.class,
                () -> bookingService.updateBookingStatus(newSecondUser.getId(), booking.getId(), true));
        assertEquals("Действие запрещено: доступно только владельцу вещи.", exception.getMessage());

        Booking checkBooking = bookingService.updateBookingStatus(newUser.getId(), booking.getId(), true);

        TypedQuery<Booking> query = em.createQuery("Select b from Booking b where b.id = :id", Booking.class);
        Booking newBooking = query
                .setParameter("id", booking.getId())
                .getSingleResult();

        assertThat(newBooking.getId(), notNullValue());
        assertThat(newBooking.getId(), equalTo(checkBooking.getId()));
        assertThat(newBooking.getItem(), equalTo(checkBooking.getItem()));
        assertThat(newBooking.getBooker(), equalTo(checkBooking.getBooker()));
        assertThat(newBooking.getStatus(), equalTo(checkBooking.getStatus()));
        assertThat(newBooking.getStatus(), equalTo(Status.APPROVED));

        final BookingValidationException exception2 = assertThrows(
                BookingValidationException.class,
                () -> bookingService.updateBookingStatus(newUser.getId(), booking.getId(), true));
        assertEquals(String.format("Недопустимое действие: бронирование c id #%d уже было одобрено.", booking.getId()), exception2.getMessage());

        checkBooking = bookingService.updateBookingStatus(newUser.getId(), booking.getId(), false);

        TypedQuery<Booking> query2 = em.createQuery("Select b from Booking b where b.id = :id", Booking.class);
        Booking newBooking2 = query2
                .setParameter("id", booking.getId())
                .getSingleResult();

        assertThat(newBooking2.getId(), notNullValue());
        assertThat(newBooking2.getId(), equalTo(checkBooking.getId()));
        assertThat(newBooking2.getItem(), equalTo(checkBooking.getItem()));
        assertThat(newBooking2.getBooker(), equalTo(checkBooking.getBooker()));
        assertThat(newBooking2.getStatus(), equalTo(checkBooking.getStatus()));
        assertThat(newBooking2.getStatus(), equalTo(Status.REJECTED));

        final BookingValidationException exception3 = assertThrows(
                BookingValidationException.class,
                () -> bookingService.updateBookingStatus(newUser.getId(), booking.getId(), false));
        assertEquals(String.format("Недопустимое действие: бронирование c id #%d уже было отклонено.", booking.getId()), exception3.getMessage());

        final BookingValidationException exception4 = assertThrows(
                BookingValidationException.class,
                () -> bookingService.updateBookingStatus(newUser.getId(), booking.getId(), true));
        assertEquals(String.format("Недопустимое действие: бронирование c id #%d уже было отклонено.", booking.getId()), exception4.getMessage());
    }

    @Test
    void getBookingByIdBasicTest() throws Exception {
        User newUser = userService.createUser(user);
        User newSecondUser = userService.createUser(secondUser);
        ItemDtoForUser itemDto = itemService.createItem(newUser.getId(), item);
        BookingDtoFromUser bookingDto = new BookingDtoFromUser(itemDto.getId(), LocalDateTime.now().plusDays(1), LocalDateTime.now().plusDays(2), null, null, null);
        Booking booking = bookingService.createBooking(newSecondUser.getId(), bookingDto);

        Booking checkBooking = bookingService.getBookingById(newUser.getId(), booking.getId());

        TypedQuery<Booking> query = em.createQuery("Select b from Booking b where b.id = :id", Booking.class);
        Booking newBooking = query
                .setParameter("id", booking.getId())
                .getSingleResult();

        assertThat(newBooking.getId(), notNullValue());
        assertThat(newBooking.getId(), equalTo(checkBooking.getId()));
        assertThat(newBooking.getItem(), equalTo(checkBooking.getItem()));
        assertThat(newBooking.getBooker(), equalTo(checkBooking.getBooker()));
        assertThat(newBooking.getStatus(), equalTo(checkBooking.getStatus()));
    }

    @Test
    void getBookingByIdByWrongUserTest() throws Exception {
        User newUser = userService.createUser(user);
        User newSecondUser = userService.createUser(secondUser);
        User newThirdUser = userService.createUser(new User("Oleg", "oleg@email.ru"));
        ItemDtoForUser itemDto = itemService.createItem(newUser.getId(), item);
        BookingDtoFromUser bookingDto = new BookingDtoFromUser(itemDto.getId(), LocalDateTime.now().plusDays(1), LocalDateTime.now().plusDays(2), null, null, null);
        Booking booking = bookingService.createBooking(newSecondUser.getId(), bookingDto);

        final BookingNotFoundException exception = assertThrows(
                BookingNotFoundException.class,
                () -> bookingService.getBookingById(newThirdUser.getId(), booking.getId()));
        assertEquals("Доступ к информации закрыт: предоставляется либо владельцу вещи, либо автору бронирования.", exception.getMessage());
    }

    @Test
    void getBookingByIdWithIncorrectIdTest() throws Exception {
        Long id = 999L;
        User newUser = userService.createUser(user);
        User newSecondUser = userService.createUser(secondUser);
        ItemDtoForUser itemDto = itemService.createItem(newUser.getId(), item);
        BookingDtoFromUser bookingDto = new BookingDtoFromUser(itemDto.getId(), LocalDateTime.now().plusDays(1), LocalDateTime.now().plusDays(2), null, null, null);
        bookingService.createBooking(newSecondUser.getId(), bookingDto);

        final BookingNotFoundException exception = assertThrows(
                BookingNotFoundException.class,
                () -> bookingService.getBookingById(newSecondUser.getId(), id));
        assertEquals(String.format("Бронирование с id #%d отсутствует в базе.", id), exception.getMessage());
    }

    @Test
    void getBookingsOfUserWithDifferentTimeStatesTest() throws Exception {
        User newUser = userService.createUser(user);
        User newSecondUser = userService.createUser(secondUser);
        ItemDtoForUser itemDto = itemService.createItem(newUser.getId(), item);
        ItemDtoForUser itemDto2 = itemService.createItem(newUser.getId(), item2);
        BookingDtoFromUser bookingDto = new BookingDtoFromUser(itemDto.getId(), LocalDateTime.now().plusDays(1), LocalDateTime.now().plusDays(2), null, null, null);
        BookingDtoFromUser bookingDto2 = new BookingDtoFromUser(itemDto2.getId(), LocalDateTime.now().plusDays(3), LocalDateTime.now().plusDays(4), null, null, null);
        List<Booking> bookings = new ArrayList<>();
        Booking booking1 = bookingService.createBooking(newSecondUser.getId(), bookingDto);
        Booking booking2 = bookingService.createBooking(newSecondUser.getId(), bookingDto2);

        bookings.add(booking1);
        bookings.add(booking2);

        Collection<Booking> targetBookings = bookingService.getBookingsOfUser(GetBookingRequest.of(newSecondUser.getId(), State.ALL, 0, 20));

        assertThat(targetBookings, hasSize(bookings.size()));

        for (Booking data : bookings) {
            assertThat(targetBookings, hasItem(allOf(
                    hasProperty("id", notNullValue()),
                    hasProperty("id", equalTo(data.getId())),
                    hasProperty("item", equalTo(data.getItem())),
                    hasProperty("booker", equalTo(data.getBooker())),
                    hasProperty("status", equalTo(data.getStatus()))
            )));
        }

        targetBookings = bookingService.getBookingsOfUser(GetBookingRequest.of(newSecondUser.getId(), State.FUTURE, 0, 20));
        assertThat(targetBookings, hasSize(bookings.size()));

        for (Booking data : bookings) {
            assertThat(targetBookings, hasItem(allOf(
                    hasProperty("id", notNullValue()),
                    hasProperty("id", equalTo(data.getId())),
                    hasProperty("item", equalTo(data.getItem())),
                    hasProperty("booker", equalTo(data.getBooker())),
                    hasProperty("status", equalTo(data.getStatus()))
            )));
        }

        targetBookings = bookingService.getBookingsOfUser(GetBookingRequest.of(newSecondUser.getId(), State.CURRENT, 0, 20));
        assertThat(targetBookings, hasSize(0));

        targetBookings = bookingService.getBookingsOfUser(GetBookingRequest.of(newSecondUser.getId(), State.PAST, 0, 20));
        assertThat(targetBookings, hasSize(0));
    }

    @Test
    void getBookingsOfUserWithDPaginationTest() throws Exception {
        User newUser = userService.createUser(user);
        User newSecondUser = userService.createUser(secondUser);
        ItemDtoForUser itemDto = itemService.createItem(newUser.getId(), item);

        BookingDtoFromUser bookingDto = new BookingDtoFromUser(itemDto.getId(), LocalDateTime.now().plusDays(1), LocalDateTime.now().plusDays(2), null, null, null);
        BookingDtoFromUser bookingDto2 = new BookingDtoFromUser(itemDto.getId(), LocalDateTime.now().plusDays(3), LocalDateTime.now().plusDays(4), null, null, null);
        BookingDtoFromUser bookingDto3 = new BookingDtoFromUser(itemDto.getId(), LocalDateTime.now().plusDays(5), LocalDateTime.now().plusDays(6), null, null, null);

        List<Booking> bookings = new ArrayList<>();
        Booking booking1 = bookingService.createBooking(newSecondUser.getId(), bookingDto);
        Booking booking2 = bookingService.createBooking(newSecondUser.getId(), bookingDto2);
        Booking booking3 = bookingService.createBooking(newSecondUser.getId(), bookingDto3);

        bookings.add(booking1);
        bookings.add(booking2);
        bookings.add(booking3);

        Collection<Booking> targetBookings = bookingService.getBookingsOfUser(GetBookingRequest.of(newSecondUser.getId(), State.ALL, 0, 1));
        assertThat(targetBookings, hasSize(1));

        targetBookings = bookingService.getBookingsOfUser(GetBookingRequest.of(newSecondUser.getId(), State.ALL, 1, 1));
        assertThat(targetBookings, hasSize(1));

        targetBookings = bookingService.getBookingsOfUser(GetBookingRequest.of(newSecondUser.getId(), State.ALL, 2, 1));
        assertThat(targetBookings, hasSize(1));

        targetBookings = bookingService.getBookingsOfUser(GetBookingRequest.of(newSecondUser.getId(), State.ALL, 0, 2));
        assertThat(targetBookings, hasSize(2));

        targetBookings = bookingService.getBookingsOfUser(GetBookingRequest.of(newSecondUser.getId(), State.ALL, 0, 3));
        assertThat(targetBookings, hasSize(3));

        targetBookings = bookingService.getBookingsOfUser(GetBookingRequest.of(newSecondUser.getId(), State.ALL, 3, 1));
        assertThat(targetBookings, hasSize(0));
    }

    @Test
    void getBookingsOfUserWithDifferentApproveStatusesTest() throws Exception {
        User newUser = userService.createUser(user);
        User newSecondUser = userService.createUser(secondUser);
        ItemDtoForUser itemDto = itemService.createItem(newUser.getId(), item);
        ItemDtoForUser itemDto2 = itemService.createItem(newUser.getId(), item2);
        BookingDtoFromUser bookingDto = new BookingDtoFromUser(itemDto.getId(), LocalDateTime.now().plusDays(1), LocalDateTime.now().plusDays(2), null, null, null);
        BookingDtoFromUser bookingDto2 = new BookingDtoFromUser(itemDto2.getId(), LocalDateTime.now().plusDays(3), LocalDateTime.now().plusDays(4), null, null, null);
        List<Booking> bookings = new ArrayList<>();
        Booking booking1 = bookingService.createBooking(newSecondUser.getId(), bookingDto);
        Booking booking2 = bookingService.createBooking(newSecondUser.getId(), bookingDto2);

        bookings.add(booking1);
        bookings.add(booking2);

        Collection<Booking> targetBookings = bookingService.getBookingsOfUser(GetBookingRequest.of(newSecondUser.getId(), State.WAITING, 0, 20));

        assertThat(targetBookings, hasSize(bookings.size()));

        for (Booking data : bookings) {
            assertThat(targetBookings, hasItem(allOf(
                    hasProperty("id", notNullValue()),
                    hasProperty("id", equalTo(data.getId())),
                    hasProperty("item", equalTo(data.getItem())),
                    hasProperty("booker", equalTo(data.getBooker())),
                    hasProperty("status", equalTo(data.getStatus()))
            )));
        }

        targetBookings = bookingService.getBookingsOfUser(GetBookingRequest.of(newSecondUser.getId(), State.REJECTED, 0, 20));
        assertThat(targetBookings, hasSize(0));

        bookingService.updateBookingStatus(newUser.getId(), booking1.getId(), false);
        targetBookings = bookingService.getBookingsOfUser(GetBookingRequest.of(newSecondUser.getId(), State.REJECTED, 0, 20));
        assertThat(targetBookings, hasSize(1));

        bookingService.updateBookingStatus(newUser.getId(), booking2.getId(), false);
        targetBookings = bookingService.getBookingsOfUser(GetBookingRequest.of(newSecondUser.getId(), State.REJECTED, 0, 20));
        assertThat(targetBookings, hasSize(2));

        /*final BookingValidationException exception = assertThrows(
                BookingValidationException.class,
                () -> bookingService.getBookingsOfUser(GetBookingRequest.of(newSecondUser.getId(), State.UNSUPPORTED_STATUS, 0, 20)));
        BookingErrorResponse bookingErrorResponse = new BookingErrorResponse(exception.getMessage());
        assertEquals(bookingErrorResponse.getError(), exception.getMessage());*/
    }

    @Test
    void getBookingsOfItemsOfUserTest() throws Exception {
        User newUser = userService.createUser(user);
        User newSecondUser = userService.createUser(secondUser);
        ItemDtoForUser itemDto = itemService.createItem(newUser.getId(), item);
        ItemDtoForUser itemDto2 = itemService.createItem(newUser.getId(), item2);
        BookingDtoFromUser bookingDto = new BookingDtoFromUser(itemDto.getId(), LocalDateTime.now().plusDays(1), LocalDateTime.now().plusDays(2), null, null, null);
        BookingDtoFromUser bookingDto2 = new BookingDtoFromUser(itemDto2.getId(), LocalDateTime.now().plusDays(3), LocalDateTime.now().plusDays(4), null, null, null);
        List<Booking> bookings = new ArrayList<>();
        Booking booking1 = bookingService.createBooking(newSecondUser.getId(), bookingDto);
        Booking booking2 = bookingService.createBooking(newSecondUser.getId(), bookingDto2);

        bookings.add(booking1);
        bookings.add(booking2);

        Collection<Booking> targetBookings = bookingService.getBookingsOfItemsOfUser(GetBookingRequest.of(newUser.getId(), State.ALL, 0, 20));

        assertThat(targetBookings, hasSize(bookings.size()));

        for (Booking data : bookings) {
            assertThat(targetBookings, hasItem(allOf(
                    hasProperty("id", notNullValue()),
                    hasProperty("id", equalTo(data.getId())),
                    hasProperty("item", equalTo(data.getItem())),
                    hasProperty("booker", equalTo(data.getBooker())),
                    hasProperty("status", equalTo(data.getStatus()))
            )));
        }
    }

    @Test
    void deleteAllTest() throws Exception {
        User newUser = userService.createUser(user);
        User newSecondUser = userService.createUser(secondUser);
        ItemDtoForUser itemDto = itemService.createItem(newUser.getId(), item);
        BookingDtoFromUser bookingDto = new BookingDtoFromUser(itemDto.getId(), LocalDateTime.now().plusDays(1), LocalDateTime.now().plusDays(2), null, null, null);
        Booking booking = bookingService.createBooking(newSecondUser.getId(), bookingDto);

        bookingService.deleteAll();

        final NoResultException exception = assertThrows(
                NoResultException.class,
                () -> {
                    TypedQuery<Booking> query = em.createQuery("Select b from Booking b where b.id = :id", Booking.class);
                    query
                            .setParameter("id", booking.getId())
                            .getSingleResult();
                });
        assertEquals("No entity found for query", exception.getMessage());
    }
}