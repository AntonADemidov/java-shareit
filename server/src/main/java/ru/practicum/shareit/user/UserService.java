package ru.practicum.shareit.user;

import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.user.dto.User;
import ru.practicum.shareit.user.dto.UserDtoFromUser;

import java.util.Collection;

@Transactional(readOnly = true)
public interface UserService {
    @Transactional
    User createUser(User user) throws Exception;

    Collection<User> findAllUsers();

    User getUserById(Long id);

    @Transactional
    User updateUser(UserDtoFromUser userDtoFromUser, Long id) throws Exception;

    @Transactional
    void deleteUser(Long id);

    @Transactional
    void deleteAll();
}