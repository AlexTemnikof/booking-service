package com.example.booking.core.booking;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;
import reactor.util.retry.Retry;

import java.time.Duration;
import java.time.LocalDate;
import java.time.OffsetDateTime;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.UUID;

@Service
public class BookingServiceImpl {
    private static final Logger log = LoggerFactory.getLogger(BookingServiceImpl.class);

    private final BookingRepository bookingRepository;
    private final WebClient webClient;
    private final String hotelBaseUrl;
    private final int retries;
    private final Duration timeout;

    public BookingServiceImpl(
            BookingRepository bookingRepository,
            WebClient.Builder builder,
            @Value("${hotel.base-url:http://hotel-service}") String hotelBaseUrl,
            @Value("${hotel.request-timeout:5000}") int timeoutMs,
            @Value("${hotel.request-retries:3}") int retries) {
        this.bookingRepository = bookingRepository;
        this.webClient = builder.baseUrl(hotelBaseUrl).build();
        this.hotelBaseUrl = hotelBaseUrl;
        this.retries = retries;
        this.timeout = Duration.ofMillis(timeoutMs);
    }

    @Transactional
    public Booking createBooking(Long userId, Long roomId, LocalDate start, LocalDate end, String requestId) {
        final Booking existingBooking = findExistingBooking(requestId);
        if (existingBooking != null) {
            return existingBooking;
        }

        final Booking booking = createPendingBooking(userId, roomId, start, end, requestId);
        return processBookingFlow(booking, roomId);
    }

    private Booking findExistingBooking(String requestId) {
        return bookingRepository.findByRequestId(requestId).orElse(null);
    }

    private Booking createPendingBooking(Long userId, Long roomId, LocalDate start, LocalDate end, String requestId) {
        final String correlationId = generateCorrelationId();

        final Booking booking = new Booking()
                .withRequestId(requestId)
                .withUserId(userId)
                .withRoomId(roomId)
                .withStartDate(start)
                .withEndDate(end)
                .withStatus(BookingStatus.PENDING)
                .withCorrelationId(correlationId)
                .withCreatedAt(OffsetDateTime.now());

        final Booking savedBooking = bookingRepository.save(booking);
        log.info("[{}] booking pending created", correlationId);

        return savedBooking;
    }

    private Booking processBookingFlow(Booking booking, Long roomId) {
        try {
            executeHotelOperations(booking, roomId);
            updateBookingStatus(booking, BookingStatus.CONFIRMED);
            log.info("[{}] booking confirmed", booking.getCorrelationId());
        } catch (Exception e) {
            handleBookingFailure(booking, roomId, e);
        }

        return booking;
    }

    private void executeHotelOperations(Booking booking, Long roomId) {
        final Map<String, String> holdPayload = createHoldPayload(booking);
        callHotel("/rooms/" + roomId + "/hold", holdPayload, booking.getCorrelationId()).block(timeout);

        final Map<String, String> confirmPayload = createConfirmPayload(booking);
        callHotel("/rooms/" + roomId + "/confirm", confirmPayload, booking.getCorrelationId()).block(timeout);
    }

    private void handleBookingFailure(Booking booking, Long roomId, Exception exception) {
        final String correlationId = booking.getCorrelationId();

        log.warn("[{}] Booking flow failed: {}", correlationId, exception.toString());

        performCompensation(booking, roomId);
        updateBookingStatus(booking, BookingStatus.CANCELLED);

        log.info("[{}] Booking CANCELLED and compensated", correlationId);
    }

    private void performCompensation(Booking booking, Long roomId) {
        try {
            Map<String, String> releasePayload = createReleasePayload(booking);
            callHotel("/rooms/" + roomId + "/release", releasePayload, booking.getCorrelationId()).block(timeout);
        } catch (Exception ignored) {
            log.debug("[{}] Compensation call failed", booking.getCorrelationId());
        }
    }

    private void updateBookingStatus(Booking booking, BookingStatus status) {
        booking.withStatus(status);
        bookingRepository.save(booking);
    }

    private Map<String, String> createHoldPayload(Booking booking) {
        return Map.of(
                "requestId", booking.getRequestId(),
                "startDate", booking.getStartDate().toString(),
                "endDate", booking.getEndDate().toString()
        );
    }

    private Map<String, String> createConfirmPayload(Booking booking) {
        return Map.of("requestId", booking.getRequestId());
    }

    private Map<String, String> createReleasePayload(Booking booking) {
        return Map.of("requestId", booking.getRequestId());
    }

    private String generateCorrelationId() {
        return UUID.randomUUID().toString();
    }

    private Mono<String> callHotel(String path, Map<String, String> payload, String correlationId) {
        return webClient.post()
                .uri(path)
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(payload)
                .header("X-Correlation-Id", correlationId)
                .retrieve()
                .bodyToMono(String.class)
                .timeout(timeout)
                .retryWhen(Retry.backoff(retries, Duration.ofMillis(300))
                        .maxBackoff(Duration.ofSeconds(2)));
    }

    public Mono<List<RoomView>> getRoomSuggestions() {
        return webClient.get()
                .uri("/hotels/rooms")
                .retrieve()
                .bodyToFlux(RoomView.class)
                .collectList()
                .map(this::sortRoomsByPopularity);
    }

    private List<RoomView> sortRoomsByPopularity(List<RoomView> rooms) {
        return rooms.stream()
                .sorted(Comparator.comparingLong(RoomView::timesBooked)
                        .thenComparing(RoomView::id))
                .toList();
    }

    public record RoomView(Long id, String number, long timesBooked) {
    }
}