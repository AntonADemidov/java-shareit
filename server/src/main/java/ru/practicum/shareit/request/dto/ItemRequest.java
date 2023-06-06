package ru.practicum.shareit.request.dto;

import lombok.AccessLevel;
import lombok.Data;
import lombok.experimental.FieldDefaults;
import lombok.experimental.NonFinal;
import ru.practicum.shareit.user.dto.User;

import javax.persistence.*;
import java.time.LocalDateTime;

@Data
@FieldDefaults(makeFinal = true, level = AccessLevel.PRIVATE)
@Entity
@Table(name = "requests")
public class ItemRequest {
    @Id
    @NonFinal
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false)
    Long id;
    @Column(name = "description", nullable = false)
    String description;
    @ManyToOne
    @JoinColumn(name = "requester_id", nullable = false)
    User requester;
    @Column(name = "created_date", nullable = false)
    LocalDateTime created;

    public ItemRequest(String description, User requester, LocalDateTime created) {
        this.description = description;
        this.requester = requester;
        this.created = created;
    }

    public ItemRequest() {
        this.id = null;
        this.description = null;
        this.requester = null;
        this.created = null;
    }
}