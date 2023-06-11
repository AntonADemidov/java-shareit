package ru.practicum.shareit.item.dto;

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
public class ItemDtoFromUserJsonTest {
    @Autowired
    @NonFinal
    JacksonTester<ItemDtoFromUser> json;
    String name = "Дрель";
    String description = "Аккумуляторная";
    Boolean available = true;

    @Test
    void createItemDtoFromUserTest() throws Exception {
        ItemDtoFromUser item = makeItem();
        JsonContent<ItemDtoFromUser> result = json.write(item);

        assertThat(result).extractingJsonPathStringValue(TestHelper.getExpName()).isEqualTo(name);
        assertThat(result).extractingJsonPathStringValue(TestHelper.getExpDescription()).isEqualTo(description);
        assertThat(result).extractingJsonPathBooleanValue(TestHelper.getExpAvailable()).isEqualTo(available);
        assertThat(result).extractingJsonPathNumberValue(TestHelper.getExpRequestId()).isEqualTo(2);
    }

    private ItemDtoFromUser makeItem() {
        return new ItemDtoFromUser(name, description, available, 2L);
    }
}