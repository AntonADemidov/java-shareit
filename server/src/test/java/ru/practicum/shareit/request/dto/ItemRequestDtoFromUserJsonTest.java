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

import static org.assertj.core.api.Assertions.assertThat;

@JsonTest
@FieldDefaults(makeFinal = true, level = AccessLevel.PRIVATE)
public class ItemRequestDtoFromUserJsonTest {
    @Autowired
    @NonFinal
    JacksonTester<ItemRequestDtoFromUser> json;
    String description = TestHelper.getShoeBrush();

    @Test
    void createItemRequestDtoFromUserTest() throws Exception {
        ItemRequestDtoFromUser request = makeRequest();
        JsonContent<ItemRequestDtoFromUser> result = json.write(request);
        assertThat(result).extractingJsonPathStringValue(TestHelper.getExpDescription()).isEqualTo(description);
    }

    private ItemRequestDtoFromUser makeRequest() {
        ItemRequestDtoFromUser request = new ItemRequestDtoFromUser();
        request.setDescription(description);
        return request;
    }
}