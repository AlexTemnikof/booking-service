package com.example.booking.core.booking;

import jakarta.persistence.*;
import java.time.LocalDate;
import java.time.OffsetDateTime;

@Entity
public class Booking {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String requestId;

    private Long userId;
    private Long roomId;
    private LocalDate startDate;
    private LocalDate endDate;

    @Enumerated(EnumType.STRING)
    private BookingStatus status;

    private String correlationId;

    private OffsetDateTime createdAt;

    public Long getId() {
        return id;
    }

    public Booking withId(final Long id) {
        this.id = id;
        return this;
    }

    public String getRequestId() {
        return requestId;
    }

    public Booking withRequestId(final String requestId) {
        this.requestId = requestId;
        return this;
    }

    public Long getUserId() {
        return userId;
    }

    public Booking withUserId(final Long userId) {
        this.userId = userId;
        return this;
    }

    public Long getRoomId() {
        return roomId;
    }

    public Booking withRoomId(final Long roomId) {
        this.roomId = roomId;
        return this;
    }

    public LocalDate getStartDate() {
        return startDate;
    }

    public Booking withStartDate(final LocalDate startDate) {
        this.startDate = startDate;
        return this;
    }

    public LocalDate getEndDate() {
        return endDate;
    }

    public Booking withEndDate(final LocalDate endDate) {
        this.endDate = endDate;
        return this;
    }

    public BookingStatus getStatus() {
        return status;
    }

    public Booking withStatus(final BookingStatus status) {
        this.status = status;
        return this;
    }

    public String getCorrelationId() {
        return correlationId;
    }

    public Booking withCorrelationId(final String correlationId) {
        this.correlationId = correlationId;
        return this;
    }

    public OffsetDateTime getCreatedAt() {
        return createdAt;
    }

    public Booking withCreatedAt(final OffsetDateTime createdAt) {
        this.createdAt = createdAt;
        return this;
    }
}


