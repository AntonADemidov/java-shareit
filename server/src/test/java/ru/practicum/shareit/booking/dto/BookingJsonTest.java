package ru.practicum.shareit.booking.dto;

import lombok.AccessLevel;
import lombok.experimental.FieldDefaults;
import lombok.experimental.NonFinal;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.json.JsonTest;
import org.springframework.boot.test.json.JacksonTester;
import org.springframework.boot.test.json.JsonContent;
import ru.practicum.shareit.booking.model.Status;
import ru.practicum.shareit.item.dto.Item;
import ru.practicum.shareit.user.dto.User;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

import static org.assertj.core.api.Assertions.assertThat;

@JsonTest
@FieldDefaults(makeFinal = true, level = AccessLevel.PRIVATE)
public class BookingJsonTest {
    @Autowired
    @NonFinal
    JacksonTester<Booking> json;
    User user = makeUser();
    Item item = makeItem();
    LocalDateTime start = LocalDateTime.now();
    LocalDateTime end = start.plusDays(1);
    Status status = Status.WAITING;

    @Test
    void createBookingTest() throws Exception {
        Booking booking = makeBooking();

        JsonContent<Booking> result = json.write(booking);

        assertThat(result).extractingJsonPathNumberValue("$.id").isEqualTo(1);
        assertThat(result).extractingJsonPathStringValue("$.start").isEqualTo(start.format(DateTimeFormatter.ISO_LOCAL_DATE_TIME));
        assertThat(result).extractingJsonPathStringValue("$.end").isEqualTo(end.format(DateTimeFormatter.ISO_LOCAL_DATE_TIME));
        assertThat(result).extractingJsonPathStringValue("$.status").isEqualTo(status.toString());
    }

    private Item makeItem() {
        Item item = new Item("Дрель", "Аккумуляторная", true, new User());
        item.setId(1L);
        return item;
    }

    private Booking makeBooking() {
        Booking booking = new  Booking(start, end, item, user, status);
        booking.setId(1L);
        return booking;
    }

    private User makeUser() {
        return new User(1L, "user", "user@user.com");
    }
}