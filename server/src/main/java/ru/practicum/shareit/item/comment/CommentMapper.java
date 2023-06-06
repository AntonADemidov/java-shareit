package ru.practicum.shareit.item.comment;

import ru.practicum.shareit.item.dto.Item;
import ru.practicum.shareit.user.dto.User;

import java.time.LocalDateTime;

public class CommentMapper {
    public static Comment toComment(CommentDtoFromUser commentDtoFromUser, User user, Item item, LocalDateTime moment) {
        return new Comment(null, commentDtoFromUser.getText(), item, user, moment);
    }

    public static CommentDtoForUser toCommentDtoForUser(Comment newComment) {
        return new CommentDtoForUser(newComment.getId(), newComment.getText(), newComment.getAuthor().getName(),
                newComment.getCreated());
    }
}