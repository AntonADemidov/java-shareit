package ru.practicum.shareit.item.comment;

import lombok.AccessLevel;
import lombok.Data;
import lombok.experimental.FieldDefaults;
import lombok.experimental.NonFinal;
import ru.practicum.shareit.item.dto.Item;
import ru.practicum.shareit.user.dto.User;

import javax.persistence.*;
import java.time.LocalDateTime;

@Data
@FieldDefaults(makeFinal = true, level = AccessLevel.PRIVATE)
@Entity
@Table(name = "comments")
public class Comment {
    @NonFinal
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false)
    Long id;
    @Column(name = "text", nullable = false)
    String text;
    @ManyToOne
    @JoinColumn(name = "item_id", nullable = false)
    Item item;
    @OneToOne
    @JoinColumn(name = "author_id", nullable = false)
    User author;
    @Column(name = "created_date", nullable = false)
    LocalDateTime created;

    public Comment(Long id, String text, Item item, User author, LocalDateTime created) {
        this.id = id;
        this.text = text;
        this.item = item;
        this.author = author;
        this.created = created;
    }

    public Comment() {
        this.id = null;
        this.text = null;
        this.item = null;
        this.author = null;
        this.created = null;
    }
}