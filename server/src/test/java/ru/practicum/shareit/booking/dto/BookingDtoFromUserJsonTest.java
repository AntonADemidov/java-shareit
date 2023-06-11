package ru.practicum.shareit.booking.dto;

import lombok.AccessLevel;
import lombok.experimental.FieldDefaults;
import lombok.experimental.NonFinal;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.json.JsonTest;
import org.springframework.boot.test.json.JacksonTester;
import org.springframework.boot.test.json.JsonContent;
import ru.practicum.shareit.TestHelper;
import ru.practicum.shareit.booking.model.Status;
import ru.practicum.shareit.item.dto.Item;
import ru.practicum.shareit.user.dto.User;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

import static org.assertj.core.api.Assertions.assertThat;

@JsonTest
@FieldDefaults(makeFinal = true, level = AccessLevel.PRIVATE)
public class BookingDtoFromUserJsonTest {
    @Autowired
    @NonFinal
    JacksonTester<BookingDtoFromUser> json;
    User user = TestHelper.getUser1();
    Item item = makeItem();
    LocalDateTime start = LocalDateTime.now();
    LocalDateTime end = start.plusDays(1);
    Status status = Status.WAITING;

    @Test
    void createBookingTest() throws Exception {
        BookingDtoFromUser booking = makeBooking();

        JsonContent<BookingDtoFromUser> result = json.write(booking);

        //assertThat(result).extractingJsonPathNumberValue(TestHelper.getExpId()).isEqualTo(1);
        assertThat(result).extractingJsonPathNumberValue(TestHelper.getExpItemId()).isEqualTo(1);
        assertThat(result).extractingJsonPathStringValue(TestHelper.getExpStart()).isEqualTo(start.format(DateTimeFormatter.ISO_LOCAL_DATE_TIME));
        assertThat(result).extractingJsonPathStringValue(TestHelper.getExpEnd()).isEqualTo(end.format(DateTimeFormatter.ISO_LOCAL_DATE_TIME));
        assertThat(result).extractingJsonPathStringValue(TestHelper.getExpStatus()).isEqualTo(status.toString());
    }

    private Item makeItem() {
        Item item = new Item("Дрель", "Аккумуляторная", true, user);
        item.setId(1L);
        return item;
    }

    private BookingDtoFromUser makeBooking() {
        return new BookingDtoFromUser(item.getId(), start, end, item, user, status);
    }
}