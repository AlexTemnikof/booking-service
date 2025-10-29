package com.example.hotel.core.reservation;

import jakarta.persistence.*;
import java.time.LocalDate;

@Entity
public class RoomReservationLock {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String requestId;

    private Long roomId;

    private LocalDate startDate;

    private LocalDate endDate;

    @Enumerated(EnumType.STRING)
    private ReservationStatus status;

    public Long getId() {
        return id;
    }

    public RoomReservationLock withId(final Long id) {
        this.id = id;
        return this;
    }

    public String getRequestId() {
        return requestId;
    }

    public RoomReservationLock withRequestId(final String requestId) {
        this.requestId = requestId;
        return this;
    }

    public Long getRoomId() {
        return roomId;
    }

    public RoomReservationLock withRoomId(final Long roomId) {
        this.roomId = roomId;
        return this;
    }

    public LocalDate getStartDate() {
        return startDate;
    }

    public RoomReservationLock withStartDate(final LocalDate startDate) {
        this.startDate = startDate;
        return this;
    }

    public LocalDate getEndDate() {
        return endDate;
    }

    public RoomReservationLock withEndDate(final LocalDate endDate) {
        this.endDate = endDate;
        return this;
    }

    public ReservationStatus getStatus() {
        return status;
    }

    public RoomReservationLock withStatus(final ReservationStatus status) {
        this.status = status;
        return this;
    }
}


