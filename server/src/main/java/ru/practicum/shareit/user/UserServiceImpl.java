package ru.practicum.shareit.user;


import lombok.AccessLevel;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.item.ItemRepository;
import ru.practicum.shareit.item.dto.Item;
import ru.practicum.shareit.user.dto.User;
import ru.practicum.shareit.user.dto.UserDtoFromUser;
import ru.practicum.shareit.user.exception.UserNotFoundException;

import java.util.Collection;

@Service
@Slf4j
@Transactional(readOnly = true)
@FieldDefaults(makeFinal = true, level = AccessLevel.PRIVATE)
public class UserServiceImpl implements UserService {
    UserRepository userRepository;
    ItemRepository itemRepository;

    @Autowired
    public UserServiceImpl(UserRepository userRepository, ItemRepository itemRepository) {
        this.userRepository = userRepository;
        this.itemRepository = itemRepository;
    }

    @Transactional
    @Override
    public User createUser(User user) {
        User newUser = userRepository.save(user);
        log.info(String.format("Новый пользователь добавлен в базу: %s c id # %d.", newUser.getName(), newUser.getId()));
        return newUser;
    }


    @Override
    public User getUserById(Long id) {
        return userRepository.findById(id).orElseThrow(() -> new UserNotFoundException(String.format("Пользователь с id #%d отсутствует в базе.", id)));
    }

    @Transactional
    @Override
    public User updateUser(UserDtoFromUser userDtoFromUser, Long id) {
        User user = getUserById(id);
        userDtoFromUser.setId(id);

        if (userDtoFromUser.getEmail() == null) {
            userDtoFromUser.setEmail(user.getEmail());
        }

        if (userDtoFromUser.getName() == null) {
            userDtoFromUser.setName(user.getName());
        }

        User updatedUser = UserMapper.toUser(userDtoFromUser);
        User newUser = userRepository.save(updatedUser);
        log.info(String.format("Пользователь обновлен в базе: %s c id # %d.", newUser.getName(), newUser.getId()));
        return newUser;
    }

    @Override
    public Collection<User> findAllUsers() {
        return userRepository.findAll();
    }

    @Transactional
    @Override
    public void deleteUser(Long id) {
        User user = getUserById(id);
        Collection<Item> items = itemRepository.findByOwnerEquals(user);

        for (Item data : items) {
            itemRepository.deleteById(data.getId());
        }
        userRepository.deleteById(user.getId());
    }

    @Transactional
    @Override
    public void deleteAll() {
        userRepository.deleteAll();
    }
}