package ru.practicum.shareit.user;

import lombok.AccessLevel;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.user.dto.User;
import ru.practicum.shareit.user.dto.UserDtoFromUser;

import java.util.Collection;

@RestController
@RequestMapping(path = "/users")
@Slf4j
@FieldDefaults(makeFinal = true, level = AccessLevel.PRIVATE)
public class UserController {
    UserService userService;
    private static final String actionWithId = "/{id}";

    @Autowired
    public UserController(UserServiceImpl userService) {
        this.userService = userService;
    }

    @PostMapping
    public User createUser(@RequestBody User user) throws Exception {
        return userService.createUser(user);
    }

    @PatchMapping(actionWithId)
    public User updateUser(@RequestBody UserDtoFromUser userDtoFromUser, @PathVariable Long id) throws Exception {
        return userService.updateUser(userDtoFromUser, id);
    }

    @GetMapping(actionWithId)
    public User getUserById(@PathVariable Long id) {
        return userService.getUserById(id);
    }

    @DeleteMapping(actionWithId)
    public void deleteUser(@PathVariable Long id) {
        userService.deleteUser(id);
    }

    @GetMapping
    public Collection<User> findAllUsers() {
        return userService.findAllUsers();
    }
}