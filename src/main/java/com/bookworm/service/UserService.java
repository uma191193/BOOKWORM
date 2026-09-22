package com.bookworm.service;

import com.bookworm.dto.user.UpdateUserRequest;
import com.bookworm.dto.user.UserResponse;

import java.util.UUID;

public interface UserService {

    UserResponse getById(UUID id);

    UserResponse update(UUID id, UpdateUserRequest request);

    void delete(UUID id);
}
