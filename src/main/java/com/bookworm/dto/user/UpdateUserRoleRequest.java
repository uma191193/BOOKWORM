package com.bookworm.dto.user;

import com.bookworm.model.user.Role;
import jakarta.validation.constraints.NotNull;

public record UpdateUserRoleRequest(@NotNull Role role) {}
