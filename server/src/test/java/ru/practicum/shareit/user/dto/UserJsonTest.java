package ru.practicum.shareit.user.dto;

import lombok.AccessLevel;
import lombok.experimental.FieldDefaults;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.json.JsonTest;
import org.springframework.boot.test.json.JacksonTester;
import org.springframework.boot.test.json.JsonContent;
import ru.practicum.shareit.TestHelper;

import static org.assertj.core.api.Assertions.assertThat;

@JsonTest
@FieldDefaults(level = AccessLevel.PRIVATE)
public class UserJsonTest {
    @Autowired
    JacksonTester<User> json;

    @Test
    void createUserTest() throws Exception {
        User user = TestHelper.getUser1();

        JsonContent<User> result = json.write(user);

        assertThat(result).extractingJsonPathNumberValue(TestHelper.getExpId()).isEqualTo(1);
        assertThat(result).extractingJsonPathStringValue(TestHelper.getExpName()).isEqualTo("user");
        assertThat(result).extractingJsonPathStringValue(TestHelper.getExpEmail()).isEqualTo("user@user.com");
    }
}