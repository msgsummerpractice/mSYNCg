package com.example.demo.dto.request;

import jakarta.validation.constraints.NotBlank;

public record CheckInRequest(
        @NotBlank String value
) {
}