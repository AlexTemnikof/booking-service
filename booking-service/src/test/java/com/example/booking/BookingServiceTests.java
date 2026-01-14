package com.example.booking;

import com.example.booking.model.Booking;
import com.example.booking.repository.BookingRepository;
import com.example.booking.service.BookingService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.*;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class BookingServiceTests {

    @Mock
    private BookingRepository bookingRepository;

    @Mock
    private WebClient.Builder webClientBuilder;

    @Mock
    private WebClient webClient;

    private BookingService bookingService;

    private final String baseUrl = "http://hotel-service";

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);

        when(webClientBuilder.baseUrl(baseUrl)).thenReturn(webClientBuilder);
        when(webClientBuilder.build()).thenReturn(webClient);

        bookingService = new BookingService(
                bookingRepository,
                webClientBuilder,
                baseUrl,
                5000, // timeout in ms
                3     // retries
        );
    }
    @Test
    void testCreateBooking_NewBooking_Success() {
        Long userId = 1L;
        Long roomId = 101L;
        LocalDate start = LocalDate.now();
        LocalDate end = start.plusDays(2);
        String requestId = "req-123";

        when(bookingRepository.findByRequestId(requestId)).thenReturn(Optional.empty());
        when(bookingRepository.save(any())).thenAnswer(invocation -> invocation.getArgument(0));

        WebClient.RequestBodyUriSpec uriSpec = mock(WebClient.RequestBodyUriSpec.class);
        @SuppressWarnings("unchecked")
        WebClient.RequestHeadersSpec<?> headersSpec = mock(WebClient.RequestHeadersSpec.class);
        WebClient.ResponseSpec responseSpec = mock(WebClient.ResponseSpec.class);

        when(webClient.post()).thenReturn(uriSpec);

        when(uriSpec.uri(anyString())).thenReturn(uriSpec);
        when(uriSpec.contentType(any())).thenReturn(uriSpec);
        when(uriSpec.bodyValue(any(Object.class))).thenAnswer(inv -> headersSpec);
        when(headersSpec.header(anyString(), anyString())).thenAnswer(inv -> headersSpec);
        when(headersSpec.retrieve()).thenReturn(responseSpec);
        when(responseSpec.bodyToMono(String.class))
                .thenReturn(Mono.just("held"))
                .thenReturn(Mono.just("confirmed"));

        Booking booking = bookingService.createBooking(userId, roomId, start, end, requestId);

        assertNotNull(booking);
        assertEquals(Booking.Status.CONFIRMED, booking.getStatus());
        verify(bookingRepository, atLeast(2)).save(any());
    }



    @Test
    void testCreateBooking_AlreadyExists() {
        String requestId = "req-123";
        Booking existing = new Booking();
        existing.setRequestId(requestId);
        when(bookingRepository.findByRequestId(requestId)).thenReturn(Optional.of(existing));

        Booking result = bookingService.createBooking(1L, 101L, LocalDate.now(), LocalDate.now().plusDays(1), requestId);

        assertSame(existing, result);
        verify(bookingRepository, never()).save(any());
    }

    @Test
    void testCreateBooking_HotelFails_ReleaseCalled() {
        Long userId = 1L;
        Long roomId = 101L;
        LocalDate start = LocalDate.now();
        LocalDate end = start.plusDays(2);
        String requestId = "req-123";

        when(bookingRepository.findByRequestId(requestId)).thenReturn(Optional.empty());
        when(bookingRepository.save(any())).thenAnswer(invocation -> invocation.getArgument(0));

        WebClient.RequestBodyUriSpec uriSpec = mock(WebClient.RequestBodyUriSpec.class);
        WebClient.RequestHeadersSpec<?> headersSpec = mock(WebClient.RequestHeadersSpec.class);
        WebClient.ResponseSpec responseSpec = mock(WebClient.ResponseSpec.class);

        when(webClient.post()).thenAnswer(invocation -> uriSpec);
        when(uriSpec.uri(contains("/hold"))).thenAnswer(invocation -> uriSpec);
        when(uriSpec.uri(contains("/confirm"))).thenAnswer(invocation -> uriSpec);
        when(uriSpec.uri(contains("/release"))).thenAnswer(invocation -> uriSpec);
        when(uriSpec.contentType(any())).thenAnswer(invocation -> uriSpec);
        when(uriSpec.bodyValue(any())).thenAnswer(invocation -> uriSpec);
        when(uriSpec.header(anyString(), anyString())).thenAnswer(invocation -> headersSpec);
        when(headersSpec.retrieve()).thenReturn(responseSpec);
        when(responseSpec.bodyToMono(String.class))
                .thenReturn(Mono.just("held"))
                .thenReturn(Mono.error(new RuntimeException("hotel error")))
                .thenReturn(Mono.just("released"));

        Booking booking = bookingService.createBooking(userId, roomId, start, end, requestId);

        assertEquals(Booking.Status.CANCELLED, booking.getStatus());
        verify(bookingRepository, atLeast(2)).save(any());
    }

    @Test
    void testGetRoomSuggestions_Sorted() {
        BookingService.RoomView r1 = new BookingService.RoomView(1L, "101", 5);
        BookingService.RoomView r2 = new BookingService.RoomView(2L, "102", 3);
        BookingService.RoomView r3 = new BookingService.RoomView(3L, "103", 5);

        WebClient.RequestHeadersUriSpec<?> getSpec = mock(WebClient.RequestHeadersUriSpec.class);
        WebClient.RequestHeadersSpec<?> getHeadersSpec = mock(WebClient.RequestHeadersSpec.class);
        WebClient.ResponseSpec getResponse = mock(WebClient.ResponseSpec.class);

        when(webClient.get()).thenAnswer(invocation -> getSpec);
        when(getSpec.uri(anyString())).thenAnswer(invocation -> getHeadersSpec);
        when(getHeadersSpec.retrieve()).thenReturn(getResponse);
        when(getResponse.bodyToFlux(BookingService.RoomView.class))
                .thenAnswer(invocation -> reactor.core.publisher.Flux.just(r1, r2, r3));

        List<BookingService.RoomView> result = bookingService.getRoomSuggestions().block();

        assertNotNull(result);
        assertEquals(3, result.size());
        assertEquals(2L, result.get(0).id());
        assertEquals(1L, result.get(1).id());
        assertEquals(3L, result.get(2).id());
    }
}
