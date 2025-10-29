package com.example.hotel.web.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.time.LocalDate;

public record HoldRequest(
        @NotBlank String requestId,
        @NotNull LocalDate startDate,
        @NotNull LocalDate endDate
) {}
