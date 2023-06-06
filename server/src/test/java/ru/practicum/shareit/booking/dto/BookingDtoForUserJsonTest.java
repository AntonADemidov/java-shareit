package ru.practicum.shareit.booking.dto;

import lombok.AccessLevel;
import lombok.experimental.FieldDefaults;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.json.JsonTest;
import org.springframework.boot.test.json.JacksonTester;
import org.springframework.boot.test.json.JsonContent;
import ru.practicum.shareit.TestHelper;
import ru.practicum.shareit.user.dto.User;

import static org.assertj.core.api.Assertions.assertThat;

@JsonTest
@FieldDefaults(level = AccessLevel.PRIVATE)
public class BookingDtoForUserJsonTest {
    @Autowired
    JacksonTester<BookingDtoForUser> json;
    final User user = TestHelper.getUser1();

    @Test
    void createBookingTest() throws Exception {
        BookingDtoForUser booking = new BookingDtoForUser(1L, user.getId());

        JsonContent<BookingDtoForUser> result = json.write(booking);

        assertThat(result).extractingJsonPathNumberValue(TestHelper.getExpId()).isEqualTo(1);
        assertThat(result).extractingJsonPathNumberValue(TestHelper.getExpBookerId()).isEqualTo(1);
    }
}