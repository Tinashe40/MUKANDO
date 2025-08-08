package com.mukando.authservice.service;

import com.mukando.authservice.dto.LoginRequest;
import com.mukando.authservice.dto.LoginResponse;
import com.mukando.authservice.dto.RegisterRequest;
import com.mukando.authservice.dto.RegisterResponse;
import com.mukando.authservice.dto.UserDetailsResponse;

public interface AuthService {
    RegisterResponse register(RegisterRequest registerRequest);
    LoginResponse login(LoginRequest loginRequest);
    UserDetailsResponse getCurrentUser(String authHeader);
    boolean verifyPassword(String username, String password);
    void updatePassword(String username, String newPassword);
}
