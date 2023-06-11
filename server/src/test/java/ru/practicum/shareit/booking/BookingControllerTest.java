package ru.practicum.shareit.booking;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.AccessLevel;
import lombok.experimental.FieldDefaults;
import lombok.experimental.NonFinal;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import ru.practicum.shareit.TestHelper;
import ru.practicum.shareit.booking.dto.Booking;
import ru.practicum.shareit.booking.model.Status;
import ru.practicum.shareit.item.ItemMapper;
import ru.practicum.shareit.item.dto.Item;
import ru.practicum.shareit.item.dto.ItemDtoForUser;
import ru.practicum.shareit.user.dto.User;

import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Arrays;
import java.util.List;

import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@FieldDefaults(makeFinal = true, level = AccessLevel.PRIVATE)
@WebMvcTest(controllers = BookingController.class)
public class BookingControllerTest {
    @NonFinal
    @Autowired
    ObjectMapper mapper;
    @NonFinal
    @MockBean
    BookingServiceImpl bookingService;
    @NonFinal
    @Autowired
    MockMvc mvc;
    User user1 = TestHelper.getUser1();
    User user2 = TestHelper.getUser2();
    ItemDtoForUser item1 = TestHelper.getItem1();
    ItemDtoForUser item2 = TestHelper.getItem2();
    Item itemOriginal1 = ItemMapper.toItem(item1);
    Item itemOriginal2 = ItemMapper.toItem(item2);
    @NonFinal
    Long bookingId = 0L;
    @NonFinal
    LocalDateTime start = LocalDateTime.now();
    Booking booking1 = makeBooking(itemOriginal1, user2);
    Booking booking2 = makeBooking(itemOriginal2, user2);

    @Test
    void createBookingTest() throws Exception {
        when(bookingService.createBooking(anyLong(), any()))
                .thenReturn(booking1);

        mvc.perform(post(TestHelper.getActionWithBookings())
                        .content(mapper.writeValueAsString(booking1))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .header(TestHelper.getUserHeader(), booking1.getBooker().getId())
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath(TestHelper.getExpId(), is(booking1.getId()), Long.class))
                .andExpect(jsonPath(TestHelper.getExpStart(), is(booking1.getStart().format(DateTimeFormatter.ISO_LOCAL_DATE_TIME))))
                .andExpect(jsonPath(TestHelper.getExpEnd(), is(booking1.getEnd().format(DateTimeFormatter.ISO_LOCAL_DATE_TIME))))
                .andExpect(jsonPath(TestHelper.getExpStatus(), is(booking1.getStatus().toString())));
    }

    @Test
    void updateBookingStatusTest() throws Exception {
        when(bookingService.updateBookingStatus(anyLong(), anyLong(), anyBoolean()))
                .thenReturn(booking1);

        mvc.perform(patch(String.format("%s%d?%s=%b", TestHelper.getActionWithBookings(), booking1.getId(), TestHelper.getApproved(), anyBoolean()))
                        .content(mapper.writeValueAsString(booking1))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .param(TestHelper.getApproved(), eq(mapper.writeValueAsString(anyBoolean())))
                        .header(TestHelper.getUserHeader(), booking1.getBooker().getId())
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath(TestHelper.getExpId(), is(booking1.getId()), Long.class))
                .andExpect(jsonPath(TestHelper.getExpStart(), is(booking1.getStart().format(DateTimeFormatter.ISO_LOCAL_DATE_TIME))))
                .andExpect(jsonPath(TestHelper.getExpEnd(), is(booking1.getEnd().format(DateTimeFormatter.ISO_LOCAL_DATE_TIME))))
                .andExpect(jsonPath(TestHelper.getExpStatus(), is(booking1.getStatus().toString())));
    }

    @Test
    void getBookingByIdOwnerTest() throws Exception {
        when(bookingService.getBookingById(anyLong(), anyLong()))
                .thenReturn(booking1);

        mvc.perform(get(String.format("%s%d", TestHelper.getActionWithBookings(), booking1.getId()))
                        .content(mapper.writeValueAsString(booking1))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .header(TestHelper.getUserHeader(), booking1.getItem().getOwner().getId())
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath(TestHelper.getExpId(), is(booking1.getId()), Long.class))
                .andExpect(jsonPath(TestHelper.getExpStart(), is(booking1.getStart().format(DateTimeFormatter.ISO_LOCAL_DATE_TIME))))
                .andExpect(jsonPath(TestHelper.getExpEnd(), is(booking1.getEnd().format(DateTimeFormatter.ISO_LOCAL_DATE_TIME))))
                .andExpect(jsonPath(TestHelper.getExpStatus(), is(booking1.getStatus().toString())));
    }

    @Test
    void getBookingByIdBookerTest() throws Exception {
        when(bookingService.getBookingById(anyLong(), anyLong()))
                .thenReturn(booking1);

        mvc.perform(get(String.format("%s%d", TestHelper.getActionWithBookings(), booking1.getId()))
                        .content(mapper.writeValueAsString(booking1))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .header(TestHelper.getUserHeader(), booking1.getBooker().getId())
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath(TestHelper.getExpId(), is(booking1.getId()), Long.class))
                .andExpect(jsonPath(TestHelper.getExpStart(), is(booking1.getStart().format(DateTimeFormatter.ISO_LOCAL_DATE_TIME))))
                .andExpect(jsonPath(TestHelper.getExpEnd(), is(booking1.getEnd().format(DateTimeFormatter.ISO_LOCAL_DATE_TIME))))
                .andExpect(jsonPath(TestHelper.getExpStatus(), is(booking1.getStatus().toString())));
    }


    @Test
    void getBookingsOfUserTest() throws Exception {
        when(bookingService.getBookingsOfUser(any()))
                .thenReturn(List.of(booking1, booking2));

        mvc.perform(get(TestHelper.getActionWithBookings())
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .header(TestHelper.getUserHeader(), booking1.getBooker().getId())
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath(TestHelper.getExpBasic(), hasSize(2)))
                .andExpect(content().json(mapper.writeValueAsString(Arrays.asList(booking1, booking2))));
    }

    @Test
    void getBookingsOfItemsUserTest() throws Exception {
        when(bookingService.getBookingsOfItemsOfUser(any()))
                .thenReturn(List.of(booking1, booking2));

        mvc.perform(get(String.format("%sowner?state=", TestHelper.getActionWithBookings()))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .header(TestHelper.getUserHeader(), user1.getId())
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath(TestHelper.getExpBasic(), hasSize(2)))
                .andExpect(content().json(mapper.writeValueAsString(Arrays.asList(booking1, booking2))));
    }

    private Booking makeBooking(Item item, User user) {
        LocalDateTime end = start.plusDays(1);
        Booking booking = new Booking(start, end, item, user, Status.WAITING);
        booking.setId(++bookingId);
        start = end.plusDays(1);
        return booking;
    }
}