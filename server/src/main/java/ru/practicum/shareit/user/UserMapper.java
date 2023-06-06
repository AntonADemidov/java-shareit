package ru.practicum.shareit.user;

import ru.practicum.shareit.user.dto.User;
import ru.practicum.shareit.user.dto.UserDtoFromUser;

public class UserMapper {
    public static User toUser(UserDtoFromUser userDtoFromUser) {
        return new User(userDtoFromUser.getId(), userDtoFromUser.getName(), userDtoFromUser.getEmail());
    }

    public static UserDtoFromUser toUserDto(User user) {
        return new UserDtoFromUser(user.getId(), user.getName(), user.getEmail());
    }
}