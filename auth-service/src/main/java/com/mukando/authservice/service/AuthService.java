package com.mukando.authservice.service;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.mukando.authservice.dto.AuthResponse;
import com.mukando.authservice.dto.MessageResponse;
import com.mukando.authservice.dto.RegisterRequest;
import com.mukando.authservice.exception.InvalidCredentialsException;
import com.mukando.authservice.exception.RefreshTokenExpiredException;
import com.mukando.authservice.exception.UserNotFoundException;
import com.mukando.authservice.exception.UsernameAlreadyExistsException;
import com.mukando.authservice.model.RefreshToken;
import com.mukando.authservice.model.Role;
import com.mukando.authservice.model.User;
import com.mukando.authservice.repository.RefreshTokenRepository;
import com.mukando.authservice.repository.UserRepository;
import com.mukando.authservice.security.JwtUtil;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final UserRepository userRepository;
    private final RefreshTokenRepository refreshTokenRepository;
    private final RefreshTokenService refreshTokenService;
    private final PasswordEncoder passwordEncoder;
    private final JwtUtil jwtUtil;

    public AuthResponse login(String username, String password) {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new UserNotFoundException("User not found"));

        if (!passwordEncoder.matches(password, user.getPassword())) {
            throw new InvalidCredentialsException("Invalid credentials");
        }

        String accessToken = jwtUtil.generateAccessToken(user);
        RefreshToken refreshToken = refreshTokenService.createRefreshToken(user);

        return new AuthResponse(accessToken, refreshToken.getToken());
    }

    public MessageResponse register(RegisterRequest request) {
        
        if (userRepository.existsByUsername(request.getUsername())) {
            throw new UsernameAlreadyExistsException("Username already exists");
        }

        User user = User.builder()
                .username(request.getUsername())
                .password(passwordEncoder.encode(request.getPassword()))
                .email(request.getEmail())
                .role(request.getRole() != null ? request.getRole() : Role.USER)
                .build();

        userRepository.save(user);
        return new MessageResponse("User registered successfully");
    }

    public AuthResponse refreshAccessToken(String refreshTokenStr) {
        RefreshToken refreshToken = refreshTokenRepository.findByToken(refreshTokenStr)
                .orElseThrow(() -> new RefreshTokenExpiredException("Invalid refresh token"));

        refreshTokenService.verifyExpiration(refreshToken);
        User user = refreshToken.getUser();

        String newAccessToken = jwtUtil.generateAccessToken(user);
        RefreshToken newRefreshToken = refreshTokenService.createRefreshToken(user);

        return new AuthResponse(newAccessToken, newRefreshToken.getToken());
    }

    public MessageResponse logout(String username) {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new UserNotFoundException("User not found"));

        refreshTokenService.deleteByUser(user);
        return new MessageResponse("User logged out successfully");
    }
}
