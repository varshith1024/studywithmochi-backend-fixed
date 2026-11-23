package com.studywithmochi.backend.auth.dto;

// Safe public view of a user (never includes the password hash)
public record UserResponse(
        Long id,
        String name,
        String email
) {
}
