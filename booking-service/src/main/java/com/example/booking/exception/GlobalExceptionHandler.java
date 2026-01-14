package com.example.booking.exception;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.bind.support.WebExchangeBindException;
import org.springframework.web.server.ServerWebExchange;

import java.util.HashMap;
import java.util.Map;

@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(BookingServiceException.class)
    public ResponseEntity<ErrorResponse> handleBookingServiceException(
            BookingServiceException ex, ServerWebExchange exchange) {

        log.error("BookingServiceException: {} - {}", ex.getErrorCode(), ex.getMessage(), ex);

        ErrorResponse errorResponse = new ErrorResponse(
                ex.getErrorCode(),
                ex.getMessage(),
                exchange.getRequest().getURI().getPath()
        );

        return new ResponseEntity<>(errorResponse, ex.getStatus());
    }

    @ExceptionHandler(WebExchangeBindException.class)
    public ResponseEntity<ValidationErrorResponse> handleValidationExceptions(
            WebExchangeBindException ex, ServerWebExchange exchange) {

        log.error("Validation error: {}", ex.getMessage());

        Map<String, String> errors = new HashMap<>();
        ex.getBindingResult().getFieldErrors().forEach(error ->
            errors.put(error.getField(), error.getDefaultMessage())
        );

        ValidationErrorResponse response = new ValidationErrorResponse(
                "VALIDATION_ERROR",
                "Validation failed",
                exchange.getRequest().getURI().getPath(),
                errors
        );

        return ResponseEntity.badRequest().body(response);
    }

    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<ErrorResponse> handleIllegalArgumentException(
            IllegalArgumentException ex, ServerWebExchange exchange) {

        log.error("IllegalArgumentException: {}", ex.getMessage(), ex);

        ErrorResponse errorResponse = new ErrorResponse(
                "BAD_REQUEST",
                ex.getMessage(),
                exchange.getRequest().getURI().getPath()
        );

        return ResponseEntity.badRequest().body(errorResponse);
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorResponse> handleGenericException(
            Exception ex, ServerWebExchange exchange) {

        log.error("Unexpected error: {}", ex.getMessage(), ex);

        ErrorResponse errorResponse = new ErrorResponse(
                "INTERNAL_SERVER_ERROR",
                "An unexpected error occurred",
                exchange.getRequest().getURI().getPath()
        );

        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(errorResponse);
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class ErrorResponse {
        private String errorCode;
        private String message;
        private String path;
    }

    @Data
    public static class ValidationErrorResponse extends ErrorResponse {
        private Map<String, String> fieldErrors;

        public ValidationErrorResponse(String errorCode, String message, String path, Map<String, String> fieldErrors) {
            super(errorCode, message, path);
            this.fieldErrors = fieldErrors;
        }
    }
}
