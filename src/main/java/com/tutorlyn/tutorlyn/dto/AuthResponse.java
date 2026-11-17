package com.tutorlyn.tutorlyn.dto;

import jakarta.validation.constraints.NotBlank;

public record AuthResponse(
        String code,
        String message) {
}
