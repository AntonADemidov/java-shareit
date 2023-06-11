package ru.practicum.shareit.booking;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.booking.dto.BookingDtoFromUser;
import ru.practicum.shareit.booking.exception.BookingValidationException;
import ru.practicum.shareit.booking.model.Approved;
import ru.practicum.shareit.booking.model.State;

import javax.validation.Valid;
import javax.validation.constraints.Positive;
import javax.validation.constraints.PositiveOrZero;

@RestController
@RequestMapping(path = "/bookings")
@RequiredArgsConstructor
@Slf4j
@Validated
@FieldDefaults(makeFinal = true, level = AccessLevel.PRIVATE)
public class BookingController {
	BookingClient bookingClient;
	static final String actionWithId = "/{id}";
	static final String userHeader = "X-Sharer-User-Id";

	@PostMapping
	public ResponseEntity<Object> createBooking(@RequestHeader(userHeader) @Positive Long userId,
												@RequestBody @Valid BookingDtoFromUser bookingDtoFromUser) {
		log.info("Creating booking {}, userId={}", bookingDtoFromUser, userId);
		return bookingClient.createBooking(userId, bookingDtoFromUser);
	}

	@PatchMapping(actionWithId)
	public ResponseEntity<Object> updateBookingStatus(@RequestHeader(userHeader) @Positive Long userId,
											@PathVariable @Positive Long id,
											@RequestParam(value = "approved", required = false) String approvedParam)
											throws BookingValidationException {
		Approved approved = Approved.from(approvedParam)
				.orElseThrow(() -> new BookingValidationException(String.format("Unknown approved: %s", approvedParam)));
		log.info("Updating booking with bookingId={}, userId={}, status={}", id, userId, approved);
		return bookingClient.updateBookingStatus(userId, id, approved);
	}

	@GetMapping(actionWithId)
	public ResponseEntity<Object> getBookingById(@RequestHeader(userHeader) @Positive Long userId,
												 @PathVariable @Positive Long id) {
		log.info("Getting booking with bookingId={}, userId={}", id, userId);
		return bookingClient.getBookingById(userId, id);
	}

	@GetMapping
	public ResponseEntity<Object> getBookingsOfUser(@RequestHeader(userHeader) @Positive Long userId,
					@RequestParam(value = "state", defaultValue = "ALL", required = false) String stateParam,
					@RequestParam(value = "from", defaultValue = "0", required = false) @PositiveOrZero Integer from,
					@RequestParam(value = "size", defaultValue = "20", required = false) @Positive Integer size) {
		try {
			State state = State.from(stateParam)
					.orElseThrow(() -> new BookingValidationException(String.format("Unknown state: %s", stateParam)));
			log.info("Getting bookings of user with userId={} (params: state={}, from={}, size={})", userId, state, from, size);
			return bookingClient.getBookingsOfUser(userId, state, from, size);
		} catch (BookingValidationException e) {
			return new ResponseEntity<>(e, HttpStatus.BAD_REQUEST);
		}
	}

	@GetMapping("/owner")
	public ResponseEntity<Object> getBookingsOfItemsOfUser(@RequestHeader(userHeader) @Positive Long userId,
					@RequestParam(value = "state", defaultValue = "ALL", required = false) String stateParam,
					@RequestParam(value = "from", defaultValue = "0", required = false) @PositiveOrZero Integer from,
					@RequestParam(value = "size", defaultValue = "20", required = false) @Positive Integer size) {
		try {
			State state = State.from(stateParam)
					.orElseThrow(() -> new BookingValidationException(String.format("Unknown state: %s", stateParam)));
			log.info("Getting bookings of items of user with userId={} (params: state={}, from={}, size={})", userId, state, from, size);
			return bookingClient.getBookingsOfItemsOfUser(userId, state, from, size);
		} catch (BookingValidationException e) {
			return new ResponseEntity<>(e, HttpStatus.BAD_REQUEST);
		}
	}
}