package com.bookworm.service;

import com.bookworm.dto.user.UpdateUserRequest;
import com.bookworm.dto.user.UserResponse;
import com.bookworm.exception.ResourceNotFoundException;
import com.bookworm.model.user.Role;
import com.bookworm.model.user.User;
import com.bookworm.repository.UserRepository;
import com.bookworm.service.impl.UserServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("UserServiceImpl")
class UserServiceImplTest {

    @Mock UserRepository userRepository;
    @InjectMocks UserServiceImpl userService;

    private UUID userId;
    private User user;

    @BeforeEach
    void setUp() {
        userId = UUID.randomUUID();
        user = User.builder()
                .id(userId).email("alice@example.com")
                .firstName("Alice").lastName("Smith")
                .phone("9000000000").role(Role.MEMBER)
                .giftPointBalance(100)
                .createdAt(LocalDateTime.now())
                .build();
    }

    @Test
    @DisplayName("getById: returns UserResponse for existing user")
    void getById_returnsResponse() {
        when(userRepository.findById(userId)).thenReturn(Optional.of(user));

        UserResponse response = userService.getById(userId);

        assertThat(response.id()).isEqualTo(userId);
        assertThat(response.email()).isEqualTo("alice@example.com");
        assertThat(response.role()).isEqualTo(Role.MEMBER);
    }

    @Test
    @DisplayName("getById: throws ResourceNotFoundException when user missing")
    void getById_throwsWhenMissing() {
        when(userRepository.findById(userId)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> userService.getById(userId))
                .isInstanceOf(ResourceNotFoundException.class);
    }

    @Test
    @DisplayName("update: patches provided fields only")
    void update_patchesProvidedFields() {
        when(userRepository.findById(userId)).thenReturn(Optional.of(user));
        when(userRepository.save(any(User.class))).thenAnswer(inv -> inv.getArgument(0));

        UpdateUserRequest req = new UpdateUserRequest("Jane", null, null, null);
        UserResponse response = userService.update(userId, req);

        assertThat(response.firstName()).isEqualTo("Jane");
        assertThat(response.lastName()).isEqualTo("Smith"); // unchanged
    }

    @Test
    @DisplayName("delete: removes the user")
    void delete_removesUser() {
        when(userRepository.findById(userId)).thenReturn(Optional.of(user));

        userService.delete(userId);

        verify(userRepository).delete(user);
    }

    @Test
    @DisplayName("delete: throws when user not found")
    void delete_throwsWhenMissing() {
        when(userRepository.findById(userId)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> userService.delete(userId))
                .isInstanceOf(ResourceNotFoundException.class);
    }
}
