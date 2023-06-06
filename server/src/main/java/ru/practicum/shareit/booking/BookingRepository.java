package ru.practicum.shareit.booking;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.querydsl.QuerydslPredicateExecutor;
import ru.practicum.shareit.booking.dto.Booking;
import ru.practicum.shareit.item.dto.Item;
import ru.practicum.shareit.user.dto.User;

import java.time.LocalDateTime;
import java.util.List;

public interface BookingRepository extends JpaRepository<Booking, Long>, QuerydslPredicateExecutor<Booking> {
    List<Booking> findByBookerEqualsAndItemEqualsAndEndBefore(User user, Item item, LocalDateTime moment);

    @Query(value = "select * " +
            "from bookings as b " +
            "where b.item_id = ?1 " +
            "and ((cast(b.end_date as timestamp without time zone) <= cast(?2 as timestamp without time zone)) " +
            "or ((cast(b.start_date as timestamp without time zone) < cast(?2 as timestamp without time zone)) " +
            "and (cast(b.end_date as timestamp without time zone) > cast(?2 as timestamp without time zone)))) " +
            "and not b.status = ?3 " +
            "order by cast(b.end_date as timestamp without time zone) desc " +
            "limit ?4", nativeQuery = true)
    Booking findLastBooking(Long itemId, LocalDateTime moment, String status, long limit);

    @Query(value = "select * " +
            "from bookings as b " +
            "where b.item_id = ?1 " +
            "and cast(b.start_date as timestamp without time zone) >= cast(?2 as timestamp without time zone) " +
            "and not b.status = ?3 " +
            "order by cast(b.start_date as timestamp without time zone) " +
            "limit ?4", nativeQuery = true)
    Booking findNextBooking(Long itemId, LocalDateTime moment, String status, long limit);
}