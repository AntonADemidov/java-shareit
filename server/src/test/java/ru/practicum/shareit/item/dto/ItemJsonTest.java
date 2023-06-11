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
import ru.practicum.shareit.user.dto.User;

import static org.assertj.core.api.Assertions.assertThat;

@JsonTest
@FieldDefaults(makeFinal = true, level = AccessLevel.PRIVATE)
public class ItemJsonTest {
    @Autowired
    @NonFinal
    JacksonTester<Item> json;
    String name = "Дрель";
    String description = "Аккумуляторная";
    Boolean available = true;
    User user = TestHelper.getUser1();

    @Test
    void createItemTest() throws Exception {
        Item item = makeItem();
        JsonContent<Item> result = json.write(item);

        assertThat(result).extractingJsonPathNumberValue(TestHelper.getExpId()).isEqualTo(1);
        assertThat(result).extractingJsonPathStringValue(TestHelper.getExpName()).isEqualTo(name);
        assertThat(result).extractingJsonPathStringValue(TestHelper.getExpDescription()).isEqualTo(description);
        assertThat(result).extractingJsonPathBooleanValue(TestHelper.getExpAvailable()).isEqualTo(available);
    }

    private Item makeItem() {
        Item item = new Item(name, description, available, user);
        item.setId(1L);
        return item;
    }
}