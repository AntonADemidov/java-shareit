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
import ru.practicum.shareit.item.dto.ItemDtoForUser;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@JsonTest
@FieldDefaults(makeFinal = true, level = AccessLevel.PRIVATE)
public class ItemRequestDtoForUserJsonTest {
    @Autowired
    @NonFinal
    JacksonTester<ItemRequestDtoForUser> json;
    LocalDateTime created = LocalDateTime.now();
    String description = TestHelper.getShoeBrush();

    @Test
    void createItemRequestDtoForUserTest() throws Exception {
        ItemRequestDtoForUser request = makeRequest();
        JsonContent<ItemRequestDtoForUser> result = json.write(request);

        assertThat(result).extractingJsonPathNumberValue(TestHelper.getExpId()).isEqualTo(1);
        assertThat(result).extractingJsonPathStringValue(TestHelper.getExpDescription()).isEqualTo(description);
        assertThat(result).extractingJsonPathStringValue(TestHelper.getExpCreated()).isEqualTo(created.format(DateTimeFormatter.ISO_LOCAL_DATE_TIME));
    }

    private ItemRequestDtoForUser makeRequest() {
        ItemRequestDtoForUser itemRequest = new ItemRequestDtoForUser(1L, description, created);
        ItemDtoForUser item1 = TestHelper.getItemWithoutId1();
        List<ItemDtoForUser> items = new ArrayList<>();

        items.add(item1);
        itemRequest.setItems(items);
        return itemRequest;
    }
}