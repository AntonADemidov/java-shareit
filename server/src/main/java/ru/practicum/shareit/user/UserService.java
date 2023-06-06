package ru.practicum.shareit.user;

import ru.practicum.shareit.user.dto.User;
import ru.practicum.shareit.user.dto.UserDtoFromUser;

import java.util.Collection;

public interface UserService {
    User createUser(User user) throws Exception;

    Collection<User> findAllUsers();

    User getUserById(Long id);

    User updateUser(UserDtoFromUser userDtoFromUser, Long id) throws Exception;

    void deleteUser(Long id);

    void deleteAll();
}