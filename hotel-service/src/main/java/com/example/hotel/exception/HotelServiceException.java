package com.example.hotel.exception;

import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
public class HotelServiceException extends RuntimeException {
    private final HttpStatus status;
    private final String errorCode;

    public HotelServiceException(String message, HttpStatus status, String errorCode) {
        super(message);
        this.status = status;
        this.errorCode = errorCode;
    }

    public HotelServiceException(String message, Throwable cause, HttpStatus status, String errorCode) {
        super(message, cause);
        this.status = status;
        this.errorCode = errorCode;
    }

    public static HotelServiceException roomNotAvailable() {
        return new HotelServiceException(
                "Room is not available for the requested dates",
                HttpStatus.CONFLICT,
                "ROOM_NOT_AVAILABLE"
        );
    }

    public static HotelServiceException holdNotFound() {
        return new HotelServiceException(
                "Reservation hold not found",
                HttpStatus.NOT_FOUND,
                "HOLD_NOT_FOUND"
        );
    }

    public static HotelServiceException holdAlreadyReleased() {
        return new HotelServiceException(
                "Reservation hold has already been released",
                HttpStatus.CONFLICT,
                "HOLD_ALREADY_RELEASED"
        );
    }

    public static HotelServiceException entityNotFound(String entityType, Long id) {
        return new HotelServiceException(
                entityType + " with id " + id + " not found",
                HttpStatus.NOT_FOUND,
                "ENTITY_NOT_FOUND"
        );
    }
}