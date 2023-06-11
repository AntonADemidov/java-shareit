package ru.practicum.shareit.user.dto;

import lombok.AccessLevel;
import lombok.Data;
import lombok.experimental.FieldDefaults;

@Data
@FieldDefaults(level = AccessLevel.PRIVATE)
public class UserDtoFromUser {
    Long id;
    String name;
    String email;

    public UserDtoFromUser(Long id, String name, String email) {
        this.id = id;
        this.name = name;
        this.email = email;
    }

    public UserDtoFromUser() {
        this.id = null;
        this.name = null;
        this.email = null;
    }
}