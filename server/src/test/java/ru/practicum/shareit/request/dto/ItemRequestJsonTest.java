package ru.practicum.shareit.request.dto;

import lombok.AccessLevel;
import lombok.experimental.FieldDefaults;
import lombok.experimental.NonFinal;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.json.JsonTest;
import org.springframework.boot.test.json.JacksonTester;
import org.springframework.boot.test.json.JsonContent;
import ru.practicum.shareit.TestHelper;
import ru.practicum.shareit.user.dto.User;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

import static org.assertj.core.api.Assertions.assertThat;

@JsonTest
@FieldDefaults(makeFinal = true, level = AccessLevel.PRIVATE)
public class ItemRequestJsonTest {
    @Autowired
    @NonFinal
    JacksonTester<ItemRequest> json;
    User user = TestHelper.getUser1();
    LocalDateTime created = LocalDateTime.now();

    @Test
    void createItemRequestTest() throws Exception {
        ItemRequest request = makeRequest();
        JsonContent<ItemRequest> result = json.write(request);

        assertThat(result).extractingJsonPathNumberValue(TestHelper.getExpId()).isEqualTo(1);
        assertThat(result).extractingJsonPathStringValue(TestHelper.getExpDescription()).isEqualTo(TestHelper.getShoeBrush());
        assertThat(result).extractingJsonPathStringValue(TestHelper.getExpCreated()).isEqualTo(created.format(DateTimeFormatter.ISO_LOCAL_DATE_TIME));
    }

    private ItemRequest makeRequest() {
        String description = TestHelper.getShoeBrush();
        Long id = 1L;
        ItemRequest itemRequest = new ItemRequest(description, user, created);

        itemRequest.setId(id);
        return itemRequest;
    }
}