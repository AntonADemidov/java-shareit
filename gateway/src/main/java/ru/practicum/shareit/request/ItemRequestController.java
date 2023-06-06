package ru.practicum.shareit.request;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.request.dto.ItemRequestDtoFromUser;

import javax.validation.Valid;
import javax.validation.constraints.Positive;
import javax.validation.constraints.PositiveOrZero;

@RestController
@RequestMapping(path = "/requests")
@RequiredArgsConstructor
@Slf4j
@Validated
@FieldDefaults(makeFinal = true, level = AccessLevel.PRIVATE)
public class ItemRequestController {
    ItemRequestClient requestClient;
    static final String userHeader = "X-Sharer-User-Id";

    @PostMapping
    public ResponseEntity<Object> createItemRequest(@RequestHeader(userHeader) @Positive Long userId,
                                                    @RequestBody @Valid ItemRequestDtoFromUser itemRequestDtoFromUser) {
        log.info("Creating itemRequest {}, userId={}", itemRequestDtoFromUser, userId);
        return requestClient.createItemRequest(userId, itemRequestDtoFromUser);
    }

    @GetMapping
    public ResponseEntity<Object> getOwnItemRequests(@RequestHeader(userHeader) @Positive Long userId) {
        log.info("Getting own itemRequests of user with userId={}", userId);
        return requestClient.getOwnItemRequests(userId);
    }

    @GetMapping("/all")
    public ResponseEntity<Object> getAllItemRequests(@RequestHeader(userHeader) @Positive Long userId,
                        @RequestParam(value = "from", defaultValue = "0", required = false) @PositiveOrZero Integer from,
                        @RequestParam(value = "size", defaultValue = "20", required = false) @Positive Integer size) {
        log.info("Getting all itemRequests, userId={} (params: from, size)", userId);
        return requestClient.getAllItemRequests(userId, from, size);
    }

    @GetMapping("/{id}")
    public ResponseEntity<Object> getItemRequestById(@RequestHeader(userHeader) @Positive Long userId,
                                                     @PathVariable @Positive Long id) {
        log.info("Getting itemRequest with itemRequestId={}, userId={}", id, userId);
        return requestClient.getItemRequestById(userId, id);
    }
}