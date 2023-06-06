package ru.practicum.shareit.user.dto;

import lombok.AccessLevel;
import lombok.Data;
import lombok.experimental.FieldDefaults;
import lombok.experimental.NonFinal;

import javax.persistence.*;

@Data
@FieldDefaults(makeFinal = true, level = AccessLevel.PRIVATE)
@Entity
@Table(name = "users")
public class User {
    @NonFinal
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false)
    Long id;
    @Column(name = "name", nullable = false)
    String name;
    @Column(name = "email", nullable = false, unique = true)
    String email;

    public User(Long id, String name, String email) {
        this.id = id;
        this.name = name;
        this.email = email;
    }

    public User() {
        this.id = null;
        this.name = null;
        this.email = null;
    }

    public User(String name, String email) {
        this.name = name;
        this.email = email;
    }
}