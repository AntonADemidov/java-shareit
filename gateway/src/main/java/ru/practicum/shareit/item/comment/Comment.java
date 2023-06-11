package ru.practicum.shareit.item.comment;

import lombok.AccessLevel;
import lombok.Data;
import lombok.experimental.FieldDefaults;
import lombok.experimental.NonFinal;
import ru.practicum.shareit.item.dto.Item;
import ru.practicum.shareit.user.dto.User;

import javax.validation.constraints.NotNull;
import java.time.LocalDateTime;

@Data
@FieldDefaults(makeFinal = true, level = AccessLevel.PRIVATE)
public class Comment {
    @NonFinal
    Long id;
    @NotNull
    String text;
    @NotNull
    Item item;
    @NotNull
    User author;
    @NotNull
    LocalDateTime created;
}