package com.example.booking.exception;

import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
public class BookingServiceException extends RuntimeException {
    private final HttpStatus status;
    private final String errorCode;

    public BookingServiceException(String message, HttpStatus status, String errorCode) {
        super(message);
        this.status = status;
        this.errorCode = errorCode;
    }

    public BookingServiceException(String message, Throwable cause, HttpStatus status, String errorCode) {
        super(message, cause);
        this.status = status;
        this.errorCode = errorCode;
    }

    public static BookingServiceException userNotFound(String username) {
        return new BookingServiceException(
                "User with username " + username + " not found",
                HttpStatus.NOT_FOUND,
                "USER_NOT_FOUND"
        );
    }

    public static BookingServiceException badCredentials() {
        return new BookingServiceException(
                "Invalid username or password",
                HttpStatus.UNAUTHORIZED,
                "BAD_CREDENTIALS"
        );
    }

    public static BookingServiceException entityNotFound(String entityType, Long id) {
        return new BookingServiceException(
                entityType + " with id " + id + " not found",
                HttpStatus.NOT_FOUND,
                "ENTITY_NOT_FOUND"
        );
    }

    public static BookingServiceException invalidBookingRequest(String message) {
        return new BookingServiceException(
                message,
                HttpStatus.BAD_REQUEST,
                "INVALID_BOOKING_REQUEST"
        );
    }

    public static BookingServiceException hotelServiceError(String message) {
        return new BookingServiceException(
                "Error communicating with hotel service: " + message,
                HttpStatus.SERVICE_UNAVAILABLE,
                "HOTEL_SERVICE_ERROR"
        );
    }
}
