package ru.practicum.shareit.request;

import lombok.AccessLevel;
import lombok.experimental.FieldDefaults;
import lombok.experimental.NonFinal;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import ru.practicum.shareit.TestHelper;
import ru.practicum.shareit.request.dto.ItemRequest;
import ru.practicum.shareit.user.UserRepository;
import ru.practicum.shareit.user.dto.User;

import java.time.LocalDateTime;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@ExtendWith(SpringExtension.class)
@DataJpaTest
@FieldDefaults(makeFinal = true, level = AccessLevel.PRIVATE)
public class ItemRequestRepositoryTest {
    @Autowired
    @NonFinal
    UserRepository userRepository;
    @Autowired
    @NonFinal
    ItemRequestRepository itemRequestRepository;
    User user = TestHelper.getUserWithoutId1();
    User user2 = TestHelper.getUserWithoutId2();
    String description = TestHelper.getDrill();

    @AfterEach
    void deleteAll() {
        itemRequestRepository.deleteAll();
        userRepository.deleteAll();
    }

    @Test
    void injectedRepositoriesAreNotNullTest() {
        assertThat(userRepository).isNotNull();
        assertThat(itemRequestRepository).isNotNull();
    }

    @Test
    void saveItemRequestBasicTest() {
        User savedUser = userRepository.save(user);

        LocalDateTime moment = LocalDateTime.now();
        ItemRequest request = new ItemRequest(description, savedUser, moment);
        ItemRequest savedRequest = itemRequestRepository.save(request);

        assertThat(savedRequest).isNotNull();
        assertThat(savedRequest.getId()).isGreaterThan(0);
    }

    @Test
    void findByRequesterEqualsTest() {
        User savedUser = userRepository.save(user);
        User savedUser2 = userRepository.save(user2);
        LocalDateTime moment = LocalDateTime.now();

        ItemRequest request = new ItemRequest(description, savedUser, moment);
        ItemRequest savedRequest = itemRequestRepository.save(request);

        ItemRequest request2 = new ItemRequest("Ищу аккумуляторную дрель", savedUser2, moment);
        itemRequestRepository.save(request2);

        List<ItemRequest> requests = itemRequestRepository.findByRequesterEquals(savedUser);

        assertThat(requests).isNotNull();
        assertThat(requests.size()).isEqualTo(1);
        assertThat(requests.get(0)).isEqualTo(savedRequest);
    }
}