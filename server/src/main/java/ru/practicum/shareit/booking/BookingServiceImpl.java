package ru.practicum.shareit.booking;

import com.querydsl.core.types.dsl.BooleanExpression;
import lombok.AccessLevel;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.PageNumber;
import ru.practicum.shareit.booking.dto.Booking;
import ru.practicum.shareit.booking.dto.BookingDtoFromUser;
import ru.practicum.shareit.booking.dto.QBooking;
import ru.practicum.shareit.booking.exception.BookingNotFoundException;
import ru.practicum.shareit.booking.exception.BookingValidationException;
import ru.practicum.shareit.booking.model.GetBookingRequest;
import ru.practicum.shareit.booking.model.State;
import ru.practicum.shareit.booking.model.Status;
import ru.practicum.shareit.item.ItemService;
import ru.practicum.shareit.item.ItemServiceImpl;
import ru.practicum.shareit.item.dto.Item;
import ru.practicum.shareit.user.UserService;
import ru.practicum.shareit.user.UserServiceImpl;
import ru.practicum.shareit.user.dto.User;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Objects;

@Service
@Slf4j
@Transactional(readOnly = true)
@FieldDefaults(makeFinal = true, level = AccessLevel.PRIVATE)
public class BookingServiceImpl implements BookingService {
    UserService userService;
    ItemService itemService;
    BookingRepository bookingRepository;

    @Autowired
    public BookingServiceImpl(UserServiceImpl userService, ItemServiceImpl itemService, BookingRepository bookingRepository) {
        this.userService = userService;
        this.itemService = itemService;
        this.bookingRepository = bookingRepository;
    }

    @Transactional
    @Override
    public Booking createBooking(Long userId, BookingDtoFromUser bookingDtoFromUser) throws BookingValidationException {
        User user = userService.getUserById(userId);
        Item item = validateBooking(bookingDtoFromUser);

        if (Objects.equals(user.getId(), item.getOwner().getId())) {
            throw new BookingNotFoundException("Недопустимое действие: нельзя бронировать собственную вещь.");
        }

        bookingDtoFromUser.setBooker(user);
        bookingDtoFromUser.setItem(item);
        bookingDtoFromUser.setStatus(Status.WAITING);

        Booking booking = BookingMapper.toBooking(bookingDtoFromUser);
        Booking newBooking = bookingRepository.save(booking);
        log.info(String.format("Новое бронирование добавлено в базу: id # %d.", newBooking.getId()));

        return newBooking;
    }

    @Transactional
    @Override
    public Booking updateBookingStatus(Long userId, Long id, Boolean value) throws BookingValidationException {
        Booking booking = getBookingById(userId, id);
        User user = userService.getUserById(userId);

        if ((Objects.equals(booking.getItem().getOwner().getId(), user.getId()))) {
            if (value) {
                if (booking.getStatus().equals(Status.WAITING)) {
                    booking.setStatus(Status.APPROVED);
                } else if (booking.getStatus().equals(Status.APPROVED)) {
                    throw new BookingValidationException(String.format("Недопустимое действие: бронирование c id #%d уже было одобрено.", booking.getId()));
                } else {
                    throw new BookingValidationException(String.format("Недопустимое действие: бронирование c id #%d уже было отклонено.", booking.getId()));
                }
            } else {
                if ((booking.getStatus().equals(Status.WAITING)) || (booking.getStatus().equals(Status.APPROVED))) {
                    booking.setStatus(Status.REJECTED);
                } else {
                    throw new BookingValidationException(String.format("Недопустимое действие: бронирование c id #%d уже было отклонено.", booking.getId()));
                }
            }
        } else {
            throw new BookingNotFoundException("Действие запрещено: доступно только владельцу вещи.");
        }
        Booking newBooking = bookingRepository.save(booking);
        log.info(String.format("Статус бронирования обновлен в базе: id # %d, статус = %s", newBooking.getId(), newBooking.getStatus()));
        return newBooking;
    }

    @Override
    public Booking getBookingById(Long userId, Long id) {
        User user = userService.getUserById(userId);
        Booking booking = bookingRepository.findById(id).orElseThrow(() -> new BookingNotFoundException(String.format("Бронирование с id #%d отсутствует в базе.", id)));

        if ((Objects.equals(booking.getBooker().getId(), user.getId())) || (Objects.equals(booking.getItem().getOwner().getId(), user.getId()))) {
            return booking;
        } else {
            throw new BookingNotFoundException("Доступ к информации закрыт: предоставляется либо владельцу вещи, либо автору бронирования.");
        }
    }

    @Override
    public Collection<Booking> getBookingsOfUser(GetBookingRequest request) {
        User user = userService.getUserById(request.getUserId());
        QBooking booking = QBooking.booking;
        List<BooleanExpression> conditions = getStateConditions(request, booking);
        conditions.add(booking.booker.id.eq(user.getId()));
        return getBookings(request, conditions);
    }

    private List<BooleanExpression> getStateConditions(GetBookingRequest request, QBooking booking) {
        List<BooleanExpression> conditions = new ArrayList<>();
        LocalDateTime moment = LocalDateTime.now();
        State state = request.getState();

        switch (state) {
            case ALL:
                break;
            case PAST:
                conditions.add(booking.end.lt(moment));
                break;
            case CURRENT:
                conditions.add(booking.start.lt(moment));
                conditions.add(booking.end.gt(moment));
                break;
            case FUTURE:
                conditions.add(booking.start.gt(moment));
                break;
            case WAITING:
                conditions.add(booking.status.eq(Status.WAITING));
                break;
            case REJECTED:
                conditions.add(booking.status.eq(Status.REJECTED));
                break;
        }
        return conditions;
    }

    @Override
    public Collection<Booking> getBookingsOfItemsOfUser(GetBookingRequest request) {
        User user = userService.getUserById(request.getUserId());
        QBooking booking = QBooking.booking;
        List<BooleanExpression> conditions = getStateConditions(request, booking);
        conditions.add(booking.item.owner.id.eq(user.getId()));
        return getBookings(request, conditions);
    }

    private List<Booking> getBookings(GetBookingRequest request, List<BooleanExpression> conditions) {
        BooleanExpression finalCondition = conditions.stream()
                .reduce(BooleanExpression::and)
                .get();

        Sort sort = Sort.by("start").descending();
        PageRequest pageRequest = PageRequest.of(PageNumber.get(request.getFrom(), request.getSize()), request.getSize(), sort);
        Iterable<Booking> bookings = bookingRepository.findAll(finalCondition, pageRequest);
        List<Booking> bookingsFinal = new ArrayList<>();

        for (Booking data : bookings) {
            bookingsFinal.add(data);
        }
        return bookingsFinal;
    }

    private Item validateBooking(BookingDtoFromUser bookingDtoFromUser) throws BookingValidationException {
        Item item = itemService.getItemById(bookingDtoFromUser.getItemId());
        LocalDateTime start = bookingDtoFromUser.getStart();
        LocalDateTime end = bookingDtoFromUser.getEnd();

        if (!item.getAvailable()) {
            throw new BookingValidationException("Вещь недоступна для бронирования: значение available равно false.");
        }

        if (end.isBefore(start) || end.equals(start)) {
            throw new BookingValidationException("Несоответствие дат в запросе: значение END не может быть ранее или " +
                    "равно значению START.");
        }
        return item;
    }

    @Transactional
    @Override
    public void deleteAll() {
        bookingRepository.deleteAll();
    }
}