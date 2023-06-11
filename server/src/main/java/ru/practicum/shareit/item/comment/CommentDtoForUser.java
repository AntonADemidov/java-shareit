package ru.practicum.shareit.item.comment;

import lombok.AccessLevel;
import lombok.Data;
import lombok.experimental.FieldDefaults;

import java.time.LocalDateTime;

@Data
@FieldDefaults(makeFinal = true, level = AccessLevel.PRIVATE)
public class CommentDtoForUser {
    Long id;
    String text;
    String authorName;
    LocalDateTime created;
}