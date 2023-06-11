package ru.practicum.shareit.booking.dto;

import lombok.AccessLevel;
import lombok.Data;
import lombok.experimental.FieldDefaults;
import lombok.experimental.NonFinal;
import ru.practicum.shareit.booking.model.Status;
import ru.practicum.shareit.item.dto.Item;
import ru.practicum.shareit.user.dto.User;

import javax.persistence.*;
import java.time.LocalDateTime;

@Data
@FieldDefaults(makeFinal = true, level = AccessLevel.PRIVATE)
@Entity
@Table(name = "bookings")
public class Booking {
    @NonFinal
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false)
    Long id;
    @Column(name = "start_date", nullable = false)
    LocalDateTime start;
    @Column(name = "end_date", nullable = false)
    LocalDateTime end;
    @ManyToOne
    @JoinColumn(name = "item_id", nullable = false)
    Item item;
    @ManyToOne
    @JoinColumn(name = "booker_id", nullable = false)
    User booker;
    @NonFinal
    @Enumerated(EnumType.STRING)
    Status status;

    public Booking(LocalDateTime start, LocalDateTime end, Item item, User booker, Status status) {
        this.id = null;
        this.start = start;
        this.end = end;
        this.item = item;
        this.booker = booker;
        this.status = status;
    }

    public Booking() {
        this.id = null;
        this.start = null;
        this.end = null;
        this.item = null;
        this.booker = null;
        this.status = null;
    }
}