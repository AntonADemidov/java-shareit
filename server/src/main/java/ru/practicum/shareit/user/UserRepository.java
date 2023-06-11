package ru.practicum.shareit.user;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.practicum.shareit.user.dto.User;

public interface UserRepository extends JpaRepository<User, Long> {
}