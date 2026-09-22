package com.bookworm.dto.user;

import com.bookworm.model.user.Role;

import java.time.LocalDateTime;
import java.util.UUID;

public record UserResponse(
        UUID id,
        String email,
        String firstName,
        String lastName,
        String phone,
        Role role,
        int giftPointBalance,
        LocalDateTime createdAt
) {}
