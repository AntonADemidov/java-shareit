package ru.practicum.shareit.user.dto;

import lombok.AccessLevel;
import lombok.experimental.FieldDefaults;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.json.JsonTest;
import org.springframework.boot.test.json.JacksonTester;
import org.springframework.boot.test.json.JsonContent;
import ru.practicum.shareit.TestHelper;
import ru.practicum.shareit.user.UserMapper;

import static org.assertj.core.api.Assertions.assertThat;

@JsonTest
@FieldDefaults(level = AccessLevel.PRIVATE)
public class UserDtoFromUserJsonTest {
    @Autowired
    JacksonTester<UserDtoFromUser> json;

    @Test
    void createUserTest() throws Exception {
        UserDtoFromUser user = UserMapper.toUserDto(TestHelper.getUser1());

        JsonContent<UserDtoFromUser> result = json.write(user);

        assertThat(result).extractingJsonPathNumberValue(TestHelper.getExpId()).isEqualTo(1);
        assertThat(result).extractingJsonPathStringValue(TestHelper.getExpName()).isEqualTo("user");
        assertThat(result).extractingJsonPathStringValue(TestHelper.getExpEmail()).isEqualTo("user@user.com");
    }
}