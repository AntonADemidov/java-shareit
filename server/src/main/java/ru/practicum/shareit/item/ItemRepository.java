package ru.practicum.shareit.item;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.querydsl.QuerydslPredicateExecutor;
import ru.practicum.shareit.item.dto.Item;
import ru.practicum.shareit.user.dto.User;

import java.util.List;

public interface ItemRepository extends JpaRepository<Item, Long>, QuerydslPredicateExecutor<Item> {
    List<Item> findByOwnerEquals(User owner);

    @Query(value = "select * " +
            "from items as i " +
            "where i.request_id = ?1", nativeQuery = true)
    List<Item> findByRequestId(Long id);
}