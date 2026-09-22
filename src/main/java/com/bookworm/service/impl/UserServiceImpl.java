package com.bookworm.service.impl;

import com.bookworm.dto.user.UpdateUserRequest;
import com.bookworm.dto.user.UserResponse;
import com.bookworm.exception.ResourceNotFoundException;
import com.bookworm.model.user.User;
import com.bookworm.repository.UserRepository;
import com.bookworm.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;

    @Override
    public UserResponse getById(UUID id) {
        return toResponse(findOrThrow(id));
    }

    @Override
    @Transactional
    public UserResponse update(UUID id, UpdateUserRequest request) {
        User user = findOrThrow(id);
        if (request.firstName() != null) user.setFirstName(request.firstName());
        if (request.lastName() != null) user.setLastName(request.lastName());
        if (request.email() != null) user.setEmail(request.email());
        if (request.phone() != null) user.setPhone(request.phone());
        return toResponse(userRepository.save(user));
    }

    @Override
    @Transactional
    public void delete(UUID id) {
        userRepository.delete(findOrThrow(id));
    }

    private User findOrThrow(UUID id) {
        return userRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("User not found: " + id));
    }

    private UserResponse toResponse(User u) {
        return new UserResponse(u.getId(), u.getEmail(), u.getFirstName(), u.getLastName(),
                u.getPhone(), u.getRole(), u.getGiftPointBalance(), u.getCreatedAt());
    }
}
