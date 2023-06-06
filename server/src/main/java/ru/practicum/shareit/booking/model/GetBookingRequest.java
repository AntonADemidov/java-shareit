package ru.practicum.shareit.booking.model;

import lombok.AccessLevel;
import lombok.Data;
import lombok.experimental.FieldDefaults;

@Data
@FieldDefaults(level = AccessLevel.PRIVATE)
public class GetBookingRequest {
    Long userId;
    State state;
    Integer from;
    Integer size;

    public static GetBookingRequest of(Long userId, State state, Integer from, Integer size) {
        GetBookingRequest request = new GetBookingRequest();
        request.setUserId(userId);
        request.setState(state);
        request.setFrom(from);
        request.setSize(size);
        return request;
    }
}