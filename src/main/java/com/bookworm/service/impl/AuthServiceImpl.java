package com.bookworm.service.impl;

import com.bookworm.dto.auth.AuthResponse;
import com.bookworm.dto.auth.LoginRequest;
import com.bookworm.dto.auth.RegisterRequest;
import com.bookworm.exception.ConflictException;
import com.bookworm.model.user.Role;
import com.bookworm.model.user.User;
import com.bookworm.repository.UserRepository;
import com.bookworm.security.JwtService;
import com.bookworm.service.AuthService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class AuthServiceImpl implements AuthService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;
    private final AuthenticationManager authenticationManager;

    @Override
    @Transactional
    public AuthResponse register(RegisterRequest request) {
        if (userRepository.existsByEmail(request.email())) {
            throw new ConflictException("Email already registered: " + request.email());
        }
        User user = User.builder()
                .email(request.email())
                .passwordHash(passwordEncoder.encode(request.password()))
                .firstName(request.firstName())
                .lastName(request.lastName())
                .phone(request.phone())
                .role(Role.MEMBER)
                .build();
        userRepository.save(user);
        return AuthResponse.bearer(jwtService.generateToken(user.getEmail()));
    }

    @Override
    public AuthResponse login(LoginRequest request) {
        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(request.email(), request.password()));
        return AuthResponse.bearer(jwtService.generateToken(request.email()));
    }
}
