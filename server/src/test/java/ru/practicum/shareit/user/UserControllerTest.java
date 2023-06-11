package ru.practicum.shareit.user;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.AccessLevel;
import lombok.experimental.FieldDefaults;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import ru.practicum.shareit.TestHelper;
import ru.practicum.shareit.user.dto.User;

import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.List;

import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@FieldDefaults(level = AccessLevel.PRIVATE)
@WebMvcTest(controllers = UserController.class)
public class UserControllerTest {
    @Autowired
    ObjectMapper mapper;
    @MockBean
    UserServiceImpl userService;
    @Autowired
    MockMvc mvc;
    final User user1 = TestHelper.getUser1();
    final User user2 = TestHelper.getUser2();

    @Test
    void createUserTest() throws Exception {
        when(userService.createUser(any()))
                .thenReturn(user1);

        mvc.perform(post(TestHelper.getActionWithUsers())
                        .content(mapper.writeValueAsString(user1))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath(TestHelper.getExpId(), is(user1.getId()), Long.class))
                .andExpect(jsonPath(TestHelper.getExpName(), is(user1.getName())))
                .andExpect(jsonPath(TestHelper.getExpEmail(), is(user1.getEmail())));
    }

    @Test
    void updateUserTest() throws Exception {
        when(userService.updateUser(any(), anyLong()))
                .thenReturn(user1);

        mvc.perform(patch(String.format("%s%d", TestHelper.getActionWithUsers(), user1.getId()))
                        .content(mapper.writeValueAsString(user1))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath(TestHelper.getExpId(), is(user1.getId()), Long.class))
                .andExpect(jsonPath(TestHelper.getExpName(), is(user1.getName())))
                .andExpect(jsonPath(TestHelper.getExpEmail(), is(user1.getEmail())));
    }

    @Test
    void getUserByIdTest() throws Exception {
        when(userService.getUserById(anyLong()))
                .thenReturn(user1);

        mvc.perform(get(String.format("%s%d", TestHelper.getActionWithUsers(), user1.getId()))
                        .content(mapper.writeValueAsString(user1))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath(TestHelper.getExpId(), is(user1.getId()), Long.class))
                .andExpect(jsonPath(TestHelper.getExpName(), is(user1.getName())))
                .andExpect(jsonPath(TestHelper.getExpEmail(), is(user1.getEmail())));
    }

    @Test
    void findAllUsersTest() throws Exception {
        when(userService.findAllUsers())
                .thenReturn(List.of(user1, user2));

        mvc.perform(get(TestHelper.getActionWithUsers())
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath(TestHelper.getExpBasic(), hasSize(2)))
                .andExpect(content().json(mapper.writeValueAsString(Arrays.asList(user1, user2))));
    }

    @Test
    void deleteUserTest() throws Exception {
        doNothing().when(userService).deleteUser(anyLong());

        mvc.perform(delete(String.format("%s%d", TestHelper.getActionWithUsers(), anyLong()))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());
    }
}