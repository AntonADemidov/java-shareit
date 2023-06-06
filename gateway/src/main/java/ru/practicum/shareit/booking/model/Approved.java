package ru.practicum.shareit.booking.model;

import java.util.Optional;

public enum Approved {
    TRUE,
    FALSE;

    public static Optional<Approved> from(String stringApproved) {
        for (Approved approved : values()) {
            if (approved.name().equalsIgnoreCase(stringApproved)) {
                return Optional.of(approved);
            }
        }
        return Optional.empty();
    }
}