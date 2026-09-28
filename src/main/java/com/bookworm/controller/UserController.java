package com.bookworm.controller;

import com.bookworm.dto.user.UpdateUserRequest;
import com.bookworm.dto.user.UpdateUserRoleRequest;
import com.bookworm.dto.user.UserResponse;
import com.bookworm.model.user.User;
import com.bookworm.service.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springdoc.core.annotations.ParameterObject;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import java.util.UUID;

@RestController
@RequestMapping("/api/v1/users")
@RequiredArgsConstructor
@SecurityRequirement(name = "bearerAuth")
@Tag(name = "Users")
public class UserController {

    private final UserService userService;

    @GetMapping("/me")
    @Operation(summary = "Get currently authenticated user profile")
    public UserResponse getMe(@AuthenticationPrincipal User user) {
        return userService.getById(user.getId());
    }

    @GetMapping
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "List all users (admin only)")
    public Page<UserResponse> list(@ParameterObject Pageable pageable) {
        return userService.list(pageable);
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN') or #id == authentication.principal.id")
    @Operation(summary = "Get a user by ID")
    public UserResponse getById(@PathVariable UUID id) {
        return userService.getById(id);
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN') or #id == authentication.principal.id")
    @Operation(summary = "Update a user profile")
    public UserResponse update(@PathVariable UUID id,
                               @Valid @RequestBody UpdateUserRequest request) {
        return userService.update(id, request);
    }

    @PatchMapping("/{id}/role")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Change a user's role (admin only)")
    public UserResponse updateRole(@PathVariable UUID id,
                                    @Valid @RequestBody UpdateUserRoleRequest request,
                                    @AuthenticationPrincipal User admin) {
        return userService.updateRole(id, request.role(), admin.getId());
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Delete a user (admin only)")
    public void delete(@PathVariable UUID id, @AuthenticationPrincipal User admin) {
        userService.delete(id, admin.getId());
    }
}
