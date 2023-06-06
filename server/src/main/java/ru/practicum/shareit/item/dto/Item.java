package ru.practicum.shareit.item.dto;

import lombok.AccessLevel;
import lombok.Data;
import lombok.experimental.FieldDefaults;
import ru.practicum.shareit.request.dto.ItemRequest;
import ru.practicum.shareit.user.dto.User;

import javax.persistence.*;

@Data
@FieldDefaults(level = AccessLevel.PRIVATE)
@Entity
@Table(name = "items")
public class Item {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false)
    Long id;
    @Column(name = "name", nullable = false)
    final String name;
    @Column(name = "description", nullable = false)
    final String description;
    @Column(name = "is_available", nullable = false)
    final Boolean available;
    @ManyToOne
    @JoinColumn(name = "owner_id", nullable = false)
    User owner;
    @ManyToOne
    @JoinColumn(name = "request_id")
    ItemRequest request;

    public Item(String name, String description, Boolean available, User owner) {
        this.id = null;
        this.name = name;
        this.description = description;
        this.available = available;
        this.owner = owner;
        this.request = null;
    }

    public Item() {
        this.id = null;
        this.name = null;
        this.description = null;
        this.available = null;
        this.owner = null;
        this.request = null;
    }

    public Item(String name, String description, Boolean available) {
        this.id = null;
        this.name = name;
        this.description = description;
        this.available = available;
        this.owner = null;
        this.request = null;
    }
}