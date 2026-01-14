package com.example.hotel.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Future;
import jakarta.validation.constraints.FutureOrPresent;
import java.time.LocalDate;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class RoomReservationRequestDto {
    @NotBlank(message = "Request ID is required")
    private String requestId;
    
    @NotNull(message = "Start date is required")
    @FutureOrPresent(message = "Start date must be today or in the future")
    private LocalDate startDate;
    
    @NotNull(message = "End date is required")
    @Future(message = "End date must be in the future")
    private LocalDate endDate;
}