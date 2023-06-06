package ru.practicum.shareit.item.comment;

import lombok.AccessLevel;
import lombok.experimental.FieldDefaults;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import ru.practicum.shareit.TestHelper;
import ru.practicum.shareit.booking.BookingRepository;
import ru.practicum.shareit.booking.dto.Booking;
import ru.practicum.shareit.booking.model.Status;
import ru.practicum.shareit.item.ItemRepository;
import ru.practicum.shareit.item.dto.Item;
import ru.practicum.shareit.user.UserRepository;
import ru.practicum.shareit.user.dto.User;

import java.time.LocalDateTime;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@ExtendWith(SpringExtension.class)
@DataJpaTest
@FieldDefaults(level = AccessLevel.PRIVATE)
public class CommentRepositoryTest {
    @Autowired
    UserRepository userRepository;
    @Autowired
    ItemRepository itemRepository;
    @Autowired
    CommentRepository commentRepository;
    @Autowired
    BookingRepository bookingRepository;
    final User user = TestHelper.getUserWithoutId1();
    final User user2 = TestHelper.getUserWithoutId2();

    @AfterEach
    void deleteAll() {
        commentRepository.deleteAll();
        bookingRepository.deleteAll();
        itemRepository.deleteAll();
        userRepository.deleteAll();
    }

    @Test
    void injectedRepositoriesAreNotNullTest() {
        assertThat(userRepository).isNotNull();
        assertThat(itemRepository).isNotNull();
        assertThat(bookingRepository).isNotNull();
        assertThat(commentRepository).isNotNull();
    }

    @Test
    void addCommentBasicTest() {
        User savedUser = userRepository.save(user);
        User savedUser2 = userRepository.save(user2);

        Item item = new Item("Дрель", "Простая дрель", true, savedUser);
        Item savedItem = itemRepository.save(item);

        Booking booking = new Booking(LocalDateTime.now().minusDays(2), LocalDateTime.now().minusDays(1), savedItem, savedUser2, Status.WAITING);
        bookingRepository.save(booking);

        Comment comment = new Comment(null, "шикарная дрель", item, savedUser2, LocalDateTime.now());
        Comment savedComment = commentRepository.save(comment);

        assertThat(savedComment).isNotNull();
        assertThat(savedComment.getId()).isGreaterThan(0);
    }

    @Test
    void findByItemEqualsTest() {
        User savedUser = userRepository.save(user);
        User savedUser2 = userRepository.save(user2);

        Item item = new Item("Дрель", "Простая дрель", true, savedUser);
        Item savedItem = itemRepository.save(item);

        Item item2 = new Item("Дрель+", "Дрель аккумуляторная", true, savedUser);
        itemRepository.save(item2);

        Booking booking = new Booking(LocalDateTime.now().minusDays(2), LocalDateTime.now().minusDays(1), savedItem, savedUser2, Status.WAITING);
        bookingRepository.save(booking);

        Comment comment = new Comment(null, "шикарная дрель", item, savedUser2, LocalDateTime.now());
        Comment savedComment = commentRepository.save(comment);

        List<Comment> comments;
        comments = commentRepository.findByItemEquals(item);

        assertThat(comments).isNotNull();
        assertThat(savedComment).isNotNull();
        assertThat(comments.size()).isEqualTo(1);
        assertThat(comments.get(0)).isEqualTo(savedComment);

        comments = commentRepository.findByItemEquals(item2);
        assertThat(savedComment).isNotNull();
        assertThat(comments.size()).isEqualTo(0);
    }
}