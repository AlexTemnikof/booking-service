package com.example.booking.web;

import com.example.booking.core.booking.Booking;
import com.example.booking.core.booking.BookingRepository;
import com.example.booking.core.booking.BookingServiceImpl;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/booking")
@io.swagger.v3.oas.annotations.security.SecurityRequirement(name = "bearer-jwt")
public class BookingController {
    private final BookingServiceImpl bookingServiceImpl;
    private final BookingRepository bookingRepository;

    public BookingController(BookingServiceImpl bookingServiceImpl, BookingRepository bookingRepository) {
        this.bookingServiceImpl = bookingServiceImpl;
        this.bookingRepository = bookingRepository;
    }

    @PostMapping
    public Booking create(@AuthenticationPrincipal Jwt jwt, @RequestBody Map<String, String> req) {
        final Long userId = Long.parseLong(jwt.getSubject());
        final Long roomId = Long.valueOf(req.get("roomId"));
        final LocalDate start = LocalDate.parse(req.get("startDate"));
        final LocalDate end = LocalDate.parse(req.get("endDate"));
        final String requestId = req.get("requestId");
        return bookingServiceImpl.createBooking(userId, roomId, start, end, requestId);
    }

    @GetMapping
    public List<Booking> myBookings(@AuthenticationPrincipal Jwt jwt) {
        final Long userId = Long.parseLong(jwt.getSubject());
        return bookingRepository.findByUserId(userId);
    }

    @GetMapping("/suggestions")
    public reactor.core.publisher.Mono<java.util.List<BookingServiceImpl.RoomView>> suggestions() {
        return bookingServiceImpl.getRoomSuggestions();
    }

    @GetMapping("/all")
    public ResponseEntity<List<Booking>> all(@AuthenticationPrincipal Jwt jwt) {
        final String scope = jwt.getClaimAsString("scope");
        if ("ADMIN".equals(scope)) {
            return ResponseEntity.ok(bookingRepository.findAll());
        }
        return ResponseEntity.status(403).build();
    }
}


