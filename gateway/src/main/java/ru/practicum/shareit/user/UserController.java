package ru.practicum.shareit.user;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.user.dto.User;
import ru.practicum.shareit.user.dto.UserDtoFromUser;

import javax.validation.Valid;
import javax.validation.constraints.Positive;

@RestController
@RequestMapping(path = "/users")
@RequiredArgsConstructor
@Slf4j
@Validated
@FieldDefaults(makeFinal = true, level = AccessLevel.PRIVATE)
public class UserController {
    UserClient userClient;
    static final String actionWithId = "/{id}";

    @PostMapping
    public ResponseEntity<Object> createUser(@RequestBody @Valid User user) {
        log.info("Creating user {}", user);
        return userClient.createUser(user);
    }

    @PatchMapping(actionWithId)
    public ResponseEntity<Object> updateUser(@RequestBody @Valid UserDtoFromUser userDtoFromUser,
                                             @PathVariable @Positive Long id) {
        log.info("Updating user {}, userId={}", userDtoFromUser, id);
        return userClient.updateUser(userDtoFromUser, id);
    }

    @GetMapping(actionWithId)
    public ResponseEntity<Object> getUserById(@PathVariable @Positive Long id) {
        log.info("Getting user with userId={}", id);
        return userClient.getUserById(id);
    }

    @DeleteMapping(actionWithId)
    public ResponseEntity<Object> deleteUser(@PathVariable @Positive Long id) {
        log.info("Deleting user with userId={}", id);
        return userClient.deleteUser(id);
    }

    @GetMapping
    public ResponseEntity<Object> findAllUsers() {
        log.info("Getting all users");
        return userClient.findAllUsers();
    }
}