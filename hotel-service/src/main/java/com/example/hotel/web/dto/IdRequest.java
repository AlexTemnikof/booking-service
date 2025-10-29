package com.example.hotel.web.dto;

import jakarta.validation.constraints.NotBlank;

public record IdRequest(@NotBlank String requestId) {}
