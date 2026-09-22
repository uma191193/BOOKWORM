package com.bookworm.service;

import com.bookworm.dto.auth.AuthResponse;
import com.bookworm.dto.auth.LoginRequest;
import com.bookworm.dto.auth.RegisterRequest;

public interface AuthService {

    AuthResponse register(RegisterRequest request);

    AuthResponse login(LoginRequest request);
}
