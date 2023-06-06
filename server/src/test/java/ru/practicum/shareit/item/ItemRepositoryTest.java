package ru.practicum.shareit.item;

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
import ru.practicum.shareit.item.dto.Item;
import ru.practicum.shareit.request.ItemRequestRepository;
import ru.practicum.shareit.request.dto.ItemRequest;
import ru.practicum.shareit.user.UserRepository;
import ru.practicum.shareit.user.dto.User;

import java.time.LocalDateTime;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@ExtendWith(SpringExtension.class)
@DataJpaTest
@FieldDefaults(makeFinal = true, level = AccessLevel.PRIVATE)
public class ItemRepositoryTest {
    @Autowired
    @NonFinal
    ItemRepository itemRepository;
    @Autowired
    @NonFinal
    UserRepository userRepository;
    @Autowired
    @NonFinal
    ItemRequestRepository itemRequestRepository;
    User user = TestHelper.getUserWithoutId1();
    User user2 = TestHelper.getUserWithoutId2();
    User user3 = TestHelper.getUserWithoutId3();

    @AfterEach
    void deleteAll() {
        itemRequestRepository.deleteAll();
        itemRepository.deleteAll();
        userRepository.deleteAll();
    }

    @Test
    void injectedRepositoriesAreNotNullTest() {
        assertThat(itemRepository).isNotNull();
        assertThat(userRepository).isNotNull();
        assertThat(itemRequestRepository).isNotNull();
    }

    @Test
    void saveItemBasicTest() {
        User savedUser = userRepository.save(user);
        Item item = new Item("Дрель", "Простая дрель", true, savedUser);
        Item savedItem = itemRepository.save(item);

        assertThat(savedItem).isNotNull();
        assertThat(savedItem.getId()).isGreaterThan(0);
    }

    @Test
    void findByOwnerEqualsTest() {
        User savedUser = userRepository.save(user);
        User savedUser2 = userRepository.save(user2);
        Item item = new Item("Дрель", "Простая дрель", true, savedUser);
        Item item2 = new Item("Дрель+", "Дрель аккумуляторная", true, savedUser2);
        Item item3 = new Item("Дрель++", "Дрель-шуруповерт", true, savedUser2);

        itemRepository.save(item);
        Item savedItem2 = itemRepository.save(item2);
        Item savedItem3 = itemRepository.save(item3);

        List<Item> items = itemRepository.findByOwnerEquals(savedUser2);

        assertThat(items).isNotNull();
        assertThat(items.size()).isEqualTo(2);
        assertThat(items.get(0)).isEqualTo(savedItem2);
        assertThat(items.get(1)).isEqualTo(savedItem3);
    }

    @Test
    void findByItemRequestIdTest() {
        User savedUser = userRepository.save(user);
        User savedUser2 = userRepository.save(user2);
        User savedUser3 = userRepository.save(user3);

        LocalDateTime moment = LocalDateTime.now();

        ItemRequest request = new ItemRequest("Хотел бы воспользоваться обычной дрелью", savedUser2, moment);
        ItemRequest savedRequest = itemRequestRepository.save(request);

        ItemRequest request2 = new ItemRequest("Ищу аккумуляторную дрель", savedUser3, moment);
        ItemRequest savedRequest2 = itemRequestRepository.save(request2);

        Item item = new Item("Дрель", "Простая дрель", true, savedUser);
        item.setRequest(savedRequest);
        Item savedItem = itemRepository.save(item);

        Item item2 = new Item("Дрель", "Дрель аккумуляторная", true, savedUser);
        item2.setRequest(savedRequest2);
        itemRepository.save(item2);

        List<Item> items = itemRepository.findByRequestId(savedRequest.getId());

        assertThat(items).isNotNull();
        assertThat(items.size()).isEqualTo(1);
        assertThat(items.get(0)).isEqualTo(savedItem);
    }
}