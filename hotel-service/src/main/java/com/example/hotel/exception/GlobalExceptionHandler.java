package com.example.hotel.exception;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

import jakarta.servlet.http.HttpServletRequest;
import java.util.HashMap;
import java.util.Map;

@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(HotelServiceException.class)
    public ResponseEntity<ErrorResponse> handleHotelServiceException(
            HotelServiceException ex, HttpServletRequest request) {

        log.error("HotelServiceException: {} - {}", ex.getErrorCode(), ex.getMessage(), ex);

        ErrorResponse errorResponse = new ErrorResponse(
                ex.getErrorCode(),
                ex.getMessage(),
                request.getRequestURI()
        );

        return new ResponseEntity<>(errorResponse, ex.getStatus());
    }

    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ValidationErrorResponse handleValidationExceptions(
            MethodArgumentNotValidException ex, HttpServletRequest request) {

        log.error("Validation error: {}", ex.getMessage());

        Map<String, String> errors = new HashMap<>();
        ex.getBindingResult().getAllErrors().forEach(error -> {
            String fieldName = ((FieldError) error).getField();
            String errorMessage = error.getDefaultMessage();
            errors.put(fieldName, errorMessage);
        });

        return new ValidationErrorResponse(
                "VALIDATION_ERROR",
                "Validation failed",
                request.getRequestURI(),
                errors
        );
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorResponse> handleGenericException(
            Exception ex, HttpServletRequest request) {

        log.error("Unexpected error: {}", ex.getMessage(), ex);

        ErrorResponse errorResponse = new ErrorResponse(
                "INTERNAL_SERVER_ERROR",
                "An unexpected error occurred",
                request.getRequestURI()
        );

        return ResponseEntity.internalServerError().body(errorResponse);
    }

    @Data
    @AllArgsConstructor
    public static class ErrorResponse {
        private String errorCode;
        private String message;
        private String path;

        // Default constructor for Lombok
        public ErrorResponse() {
        }
    }

    @Data
    public static class ValidationErrorResponse extends ErrorResponse {
        private final Map<String, String> fieldErrors;

        public ValidationErrorResponse(String errorCode, String message, String path, Map<String, String> fieldErrors) {
            super(errorCode, message, path);
            this.fieldErrors = fieldErrors;
        }
    }
}
