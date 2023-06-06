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
public class ItemDtoFromUserCreationJsonTest {
    @Autowired
    @NonFinal
    JacksonTester<ItemDtoFromUserCreation> json;
    String name = "Дрель";
    String description = "Аккумуляторная";
    Boolean available = true;

    @Test
    void createItemDtoFromUserCreationTest() throws Exception {
        ItemDtoFromUserCreation item = makeItem();
        JsonContent<ItemDtoFromUserCreation> result = json.write(item);

        assertThat(result).extractingJsonPathStringValue(TestHelper.getExpName()).isEqualTo(name);
        assertThat(result).extractingJsonPathStringValue(TestHelper.getExpDescription()).isEqualTo(description);
        assertThat(result).extractingJsonPathBooleanValue(TestHelper.getExpAvailable()).isEqualTo(available);
        assertThat(result).extractingJsonPathNumberValue(TestHelper.getExpRequestId()).isEqualTo(2);
    }

    private ItemDtoFromUserCreation makeItem() {
        return new ItemDtoFromUserCreation(name, description, available, 2L);
    }
}