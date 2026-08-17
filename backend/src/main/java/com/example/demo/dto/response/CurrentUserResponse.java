package com.example.demo.dto.response;

import com.example.demo.model.UserRole;

public record CurrentUserResponse(
        Integer id,
        String firstName,
        String lastName,
        String email,
        UserRole role
) {
}