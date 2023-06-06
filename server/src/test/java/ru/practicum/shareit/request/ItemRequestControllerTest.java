package ru.practicum.shareit.request;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.AccessLevel;
import lombok.experimental.FieldDefaults;
import lombok.experimental.NonFinal;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import ru.practicum.shareit.TestHelper;
import ru.practicum.shareit.item.dto.ItemDtoForUser;
import ru.practicum.shareit.request.dto.ItemRequestDtoForUser;
import ru.practicum.shareit.user.dto.User;

import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.anyInt;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@FieldDefaults(makeFinal = true, level = AccessLevel.PRIVATE)
@WebMvcTest(controllers = ItemRequestController.class)
public class ItemRequestControllerTest {
    @NonFinal
    @Autowired
    ObjectMapper mapper;
    @NonFinal
    @MockBean
    ItemRequestServiceImpl itemRequestService;
    @NonFinal
    @Autowired
    MockMvc mvc;
    User user1 = TestHelper.getUser1();
    ItemDtoForUser item1 = TestHelper.getItem1();
    ItemDtoForUser item2 = TestHelper.getItem2();
    @NonFinal
    Long requestId = 0L;
    ItemRequestDtoForUser itemRequest1 = makeRequest(TestHelper.getShoeBrush(), item1);
    ItemRequestDtoForUser itemRequest2 = makeRequest("Нужен пылесос", item2);

    @Test
    void createItemRequestTest() throws Exception {
        when(itemRequestService.createItemRequest(anyLong(), any()))
                .thenReturn(itemRequest1);

        mvc.perform(post(TestHelper.getActionWithRequests())
                        .content(mapper.writeValueAsString(itemRequest1))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .header(TestHelper.getUserHeader(), user1.getId())
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath(TestHelper.getExpId(), is(itemRequest1.getId()), Long.class))
                .andExpect(jsonPath(TestHelper.getExpDescription(), is(itemRequest1.getDescription())))
                .andExpect(jsonPath(TestHelper.getExpCreated(), is(itemRequest1.getCreated().format(DateTimeFormatter.ISO_LOCAL_DATE_TIME))))
                .andExpect(jsonPath(TestHelper.getExpItems(), hasSize(1)));
    }

    @Test
    void getOwnItemRequestsTest() throws Exception {
        when(itemRequestService.getOwnItemRequests(anyLong()))
                .thenReturn(List.of(itemRequest1));

        mvc.perform(get(TestHelper.getActionWithRequests())
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .header(TestHelper.getUserHeader(), user1.getId())
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath(TestHelper.getExpBasic(), hasSize(1)))
                .andExpect(content().json(mapper.writeValueAsString(List.of(itemRequest1))));
    }

    @Test
    void getAllItemRequestsTest() throws Exception {
        when(itemRequestService.getAllItemRequests(anyLong(), anyInt(), anyInt()))
                .thenReturn(List.of(itemRequest2));

        mvc.perform(get(String.format("%sall", TestHelper.getActionWithRequests()))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .header(TestHelper.getUserHeader(), user1.getId())
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath(TestHelper.getExpBasic(), hasSize(1)))
                .andExpect(content().json(mapper.writeValueAsString(List.of(itemRequest2))));
    }

    @Test
    void getItemRequestByIdTest() throws Exception {
        when(itemRequestService.getItemRequestById(anyLong(), anyLong()))
                .thenReturn(itemRequest1);

        mvc.perform(get(String.format("%s%d", TestHelper.getActionWithRequests(), itemRequest1.getId()))
                        .content(mapper.writeValueAsString(itemRequest1))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .header(TestHelper.getUserHeader(), user1.getId())
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath(TestHelper.getExpId(), is(itemRequest1.getId()), Long.class))
                .andExpect(jsonPath(TestHelper.getExpDescription(), is(itemRequest1.getDescription())))
                .andExpect(jsonPath(TestHelper.getExpCreated(), is(itemRequest1.getCreated().format(DateTimeFormatter.ISO_LOCAL_DATE_TIME))))
                .andExpect(jsonPath(TestHelper.getExpItems(), hasSize(1)));
    }

    private ItemRequestDtoForUser makeRequest(String description, ItemDtoForUser item) {
        LocalDateTime created = LocalDateTime.now();
        List<ItemDtoForUser> items = new ArrayList<>();
        ItemRequestDtoForUser itemRequestDtoForUser = new ItemRequestDtoForUser(++requestId, description, created);

        item.setRequestId(itemRequestDtoForUser.getId());
        items.add(item);
        itemRequestDtoForUser.setItems(items);

        return itemRequestDtoForUser;
    }
}