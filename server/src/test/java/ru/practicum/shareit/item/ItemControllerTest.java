package ru.practicum.shareit.item;

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
import ru.practicum.shareit.item.comment.Comment;
import ru.practicum.shareit.item.comment.CommentDtoForUser;
import ru.practicum.shareit.item.comment.CommentMapper;
import ru.practicum.shareit.item.dto.Item;
import ru.practicum.shareit.item.dto.ItemDtoForUser;
import ru.practicum.shareit.user.dto.User;

import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Arrays;
import java.util.List;

import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@FieldDefaults(makeFinal = true, level = AccessLevel.PRIVATE)
@WebMvcTest(controllers = ItemController.class)
public class ItemControllerTest {
    @Autowired
    @NonFinal
    ObjectMapper mapper;
    @MockBean
    @NonFinal
    ItemServiceImpl itemService;
    @Autowired
    @NonFinal
    MockMvc mvc;
    User user1 = TestHelper.getUser1();
    ItemDtoForUser item1 = TestHelper.getItem1();
    ItemDtoForUser item2 = TestHelper.getItem2();
    Item itemOriginal1 = ItemMapper.toItem(item1);
    Item itemOriginal2 = ItemMapper.toItem(item2);
    @NonFinal
    Long commentId = 0L;
    Comment comment = makeComment("Add comment from user1", itemOriginal1, user1);

    @Test
    void createItemTest() throws Exception {
        when(itemService.createItem(anyLong(), any()))
                .thenReturn(item1);

        mvc.perform(post(TestHelper.getActionWithItems())
                        .content(mapper.writeValueAsString(item1))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .header(TestHelper.getUserHeader(), item1.getOwner().getId())
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath(TestHelper.getExpId(), is(item1.getId()), Long.class))
                .andExpect(jsonPath(TestHelper.getExpName(), is(item1.getName())))
                .andExpect(jsonPath(TestHelper.getExpAvailable(), is(item1.getAvailable())));
    }

    @Test
    void updateItemTest() throws Exception {
        when(itemService.updateItem(anyLong(), any(), anyLong()))
                .thenReturn(item1);

        mvc.perform(patch(String.format("%s%d", TestHelper.getActionWithItems(), item1.getId()))
                        .content(mapper.writeValueAsString(item1))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .header(TestHelper.getUserHeader(), item1.getOwner().getId())
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath(TestHelper.getExpId(), is(item1.getId()), Long.class))
                .andExpect(jsonPath(TestHelper.getExpName(), is(item1.getName())))
                .andExpect(jsonPath(TestHelper.getExpAvailable(), is(item1.getAvailable())));
    }

    @Test
    void getItemByIdTest() throws Exception {
        when(itemService.getItemDtoForUserById(anyLong(), anyLong()))
                .thenReturn(item1);

        mvc.perform(get(String.format("%s%d", TestHelper.getActionWithItems(), item1.getId()))
                        .content(mapper.writeValueAsString(item1))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .header(TestHelper.getUserHeader(), item1.getOwner().getId())
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath(TestHelper.getExpId(), is(item1.getId()), Long.class))
                .andExpect(jsonPath(TestHelper.getExpName(), is(item1.getName())))
                .andExpect(jsonPath(TestHelper.getExpAvailable(), is(item1.getAvailable())));
    }

    @Test
    void getItemsOfUserTest() throws Exception {
        when(itemService.getItemsOfUser(anyLong(), anyInt(), anyInt()))
                .thenReturn(List.of(item1, item2));

        mvc.perform(get(TestHelper.getActionWithItems())
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .header(TestHelper.getUserHeader(), item1.getOwner().getId())
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().json(mapper.writeValueAsString(Arrays.asList(item1, item2))));
    }

    @Test
    void searchItemsTest() throws Exception {
        when(itemService.searchItems(anyString(), anyInt(), anyInt()))
                .thenReturn(List.of(itemOriginal1, itemOriginal2));

        mvc.perform(get(String.format("%ssearch?%s=%s", TestHelper.getActionWithItems(), TestHelper.getText(), anyString()))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .param(TestHelper.getText(), eq(anyString()))
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath(TestHelper.getExpBasic(), hasSize(2)))
                .andExpect(status().isOk())
                .andExpect(content().json(mapper.writeValueAsString(Arrays.asList(itemOriginal1, itemOriginal2))));
    }


    @Test
    void deleteItemTest() throws Exception {
        doNothing().when(itemService).deleteItem(anyLong());

        mvc.perform(delete(String.format("%s%d", TestHelper.getActionWithItems(), anyLong()))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());
    }

    @Test
    void addCommentTest() throws Exception {
        CommentDtoForUser commentDto = CommentMapper.toCommentDtoForUser(comment);

        when(itemService.addComment(anyLong(), any(), anyLong(), any()))
                .thenReturn(commentDto);

        mvc.perform(post(String.format("%s%d/comment", TestHelper.getActionWithItems(), itemOriginal1.getId()))
                        .content(mapper.writeValueAsString(comment))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .header(TestHelper.getUserHeader(), comment.getAuthor().getId())
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath(TestHelper.getExpId(), is(commentDto.getId()), Long.class))
                .andExpect(jsonPath(TestHelper.getExpText(), is(commentDto.getText())))
                .andExpect(jsonPath(TestHelper.getExpAuthorName(), is(commentDto.getAuthorName())))
                .andExpect(jsonPath(TestHelper.getExpCreated(), is(commentDto.getCreated().format(DateTimeFormatter.ISO_LOCAL_DATE_TIME))));
    }

    private Comment makeComment(String text, Item item, User author) {
        LocalDateTime moment = LocalDateTime.now();
        return new Comment(++commentId, text, item, author, moment);
    }
}