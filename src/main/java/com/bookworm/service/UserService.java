package com.bookworm.service;

import com.bookworm.dto.user.UpdateUserRequest;
import com.bookworm.dto.user.UserResponse;
import com.bookworm.model.user.Role;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.UUID;

public interface UserService {

    UserResponse getById(UUID id);

    Page<UserResponse> list(Pageable pageable);

    UserResponse update(UUID id, UpdateUserRequest request);

    UserResponse updateRole(UUID id, Role newRole, UUID requesterId);

    void delete(UUID id, UUID requesterId);
}
