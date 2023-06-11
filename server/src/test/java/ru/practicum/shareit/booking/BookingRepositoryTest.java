package ru.practicum.shareit.booking;

import lombok.AccessLevel;
import lombok.experimental.FieldDefaults;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import ru.practicum.shareit.TestHelper;
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
public class BookingRepositoryTest {
    @Autowired
    ItemRepository itemRepository;
    @Autowired
    UserRepository userRepository;
    @Autowired
    BookingRepository bookingRepository;
    final User user = TestHelper.getUserWithoutId1();
    final User user2 = TestHelper.getUserWithoutId2();
    final User user3 = TestHelper.getUserWithoutId3();

    @AfterEach
    void deleteAll() {
        bookingRepository.deleteAll();
        itemRepository.deleteAll();
        userRepository.deleteAll();
    }

    @Test
    void injectedRepositoriesAreNotNullTest() {
        assertThat(itemRepository).isNotNull();
        assertThat(userRepository).isNotNull();
        assertThat(bookingRepository).isNotNull();
    }

    @Test
    void saveBookingBasicTest() {
        User savedUser = userRepository.save(user);
        User savedUser2 = userRepository.save(user2);
        Item item = new Item("Дрель", "Простая дрель", true, savedUser);
        Item savedItem = itemRepository.save(item);

        LocalDateTime start = LocalDateTime.now();
        LocalDateTime end = LocalDateTime.now().plusDays(1);

        Booking booking = new Booking(start, end, savedItem, savedUser2, Status.WAITING);
        Booking savedBooking = bookingRepository.save(booking);

        assertThat(savedBooking).isNotNull();
        assertThat(savedBooking.getId()).isGreaterThan(0);
    }

    @Test
    void findByBookerEqualsAndItemEqualsAndEndBeforeTest() {
        User savedUser = userRepository.save(user);
        User savedUser2 = userRepository.save(user2);
        User savedUser3 = userRepository.save(user3);

        Item item = new Item("Дрель", "Простая дрель", true, savedUser);
        Item savedItem = itemRepository.save(item);
        Item item2 = new Item("Дрель+", "Дрель аккумуляторная", true, savedUser2);
        Item savedItem2 = itemRepository.save(item2);

        LocalDateTime start = LocalDateTime.now();
        LocalDateTime end = LocalDateTime.now().plusDays(1);

        List<Booking> bookings;

        Booking booking = new Booking(start, end, savedItem, savedUser2, Status.WAITING);
        Booking savedBooking = bookingRepository.save(booking);

        Booking booking2 = new Booking(start, end, savedItem2, savedUser3, Status.WAITING);
        bookingRepository.save(booking2);

        bookings = bookingRepository.findByBookerEqualsAndItemEqualsAndEndBefore(savedUser2, savedItem, LocalDateTime.now().plusDays(1).minusSeconds(1));
        assertThat(bookings).isNotNull();
        assertThat(bookings.size()).isEqualTo(0);

        bookings = bookingRepository.findByBookerEqualsAndItemEqualsAndEndBefore(savedUser2, savedItem, LocalDateTime.now().plusDays(2));
        assertThat(bookings).isNotNull();
        assertThat(bookings.size()).isEqualTo(1);
        assertThat(bookings.get(0)).isEqualTo(savedBooking);

        bookings = bookingRepository.findByBookerEqualsAndItemEqualsAndEndBefore(savedUser3, savedItem, LocalDateTime.now().plusDays(2));
        assertThat(bookings).isNotNull();
        assertThat(bookings.size()).isEqualTo(0);

        bookings = bookingRepository.findByBookerEqualsAndItemEqualsAndEndBefore(savedUser2, savedItem2, LocalDateTime.now().plusDays(2));
        assertThat(bookings).isNotNull();
        assertThat(bookings.size()).isEqualTo(0);
    }

    @Test
    void findLastBookingTest() {
        User savedUser = userRepository.save(user);
        User savedUser2 = userRepository.save(user2);
        User savedUser3 = userRepository.save(user3);

        Item item = new Item("Дрель", "Простая дрель", true, savedUser);
        Item savedItem = itemRepository.save(item);
        Item item2 = new Item("Дрель+", "Дрель аккумуляторная", true, savedUser2);
        itemRepository.save(item2);

        Booking booking = new Booking(LocalDateTime.now(), LocalDateTime.now().plusDays(1), savedItem, savedUser2, Status.APPROVED);
        Booking savedBooking = bookingRepository.save(booking);

        Booking booking2 = new Booking(LocalDateTime.now().plusDays(2), LocalDateTime.now().plusDays(3), savedItem, savedUser3, Status.APPROVED);
        Booking savedBooking2 = bookingRepository.save(booking2);

        Booking fromRepo = bookingRepository.findLastBooking(savedItem.getId(), LocalDateTime.now().plusDays(4), "REJECTED", 1);
        assertThat(fromRepo).isNotNull();
        assertThat(fromRepo).isEqualTo(savedBooking2);

        fromRepo = bookingRepository.findLastBooking(savedItem.getId(), LocalDateTime.now().plusDays(1), "REJECTED", 1);
        assertThat(fromRepo).isNotNull();
        assertThat(fromRepo).isEqualTo(savedBooking);
    }

    @Test
    void findNextBookingTest() {
        User savedUser = userRepository.save(user);
        User savedUser2 = userRepository.save(user2);
        User savedUser3 = userRepository.save(user3);

        Item item = new Item("Дрель", "Простая дрель", true, savedUser);
        Item savedItem = itemRepository.save(item);
        Item item2 = new Item("Дрель+", "Дрель аккумуляторная", true, savedUser2);
        itemRepository.save(item2);

        Booking booking = new Booking(LocalDateTime.now(), LocalDateTime.now().plusDays(1), savedItem, savedUser2, Status.APPROVED);
        Booking savedBooking = bookingRepository.save(booking);

        Booking booking2 = new Booking(LocalDateTime.now().plusDays(2), LocalDateTime.now().plusDays(3), savedItem, savedUser3, Status.APPROVED);
        Booking savedBooking2 = bookingRepository.save(booking2);

        Booking fromRepo = bookingRepository.findNextBooking(savedItem.getId(), LocalDateTime.now().minusSeconds(1), "REJECTED", 1);
        assertThat(fromRepo).isNotNull();
        assertThat(fromRepo).isEqualTo(savedBooking);

        fromRepo = bookingRepository.findNextBooking(savedItem.getId(), LocalDateTime.now().plusDays(1), "REJECTED", 1);
        assertThat(fromRepo).isNotNull();
        assertThat(fromRepo).isEqualTo(savedBooking2);
    }
}