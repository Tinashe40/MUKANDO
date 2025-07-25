package com.mukando.authservice.service;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.mukando.authservice.dto.LoginResponse;
import com.mukando.authservice.dto.MessageResponse;
import com.mukando.authservice.dto.RegisterRequest;
import com.mukando.authservice.model.RefreshToken;
import com.mukando.authservice.model.Role;
import com.mukando.authservice.model.User;
import com.mukando.authservice.repository.RefreshTokenRepository;
import com.mukando.authservice.repository.UserRepository;
import com.mukando.authservice.security.JwtUtil;
import com.mukando.commons.exception.InvalidCredentialsException;
import com.mukando.commons.exception.RefreshTokenExpiredException;
import com.mukando.commons.exception.UserNotFoundException;
import com.mukando.commons.exception.UsernameAlreadyExistException;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final UserRepository userRepository;
    private final RefreshTokenRepository refreshTokenRepository;
    private final RefreshTokenService refreshTokenService;
    private final PasswordEncoder passwordEncoder;
    private final JwtUtil jwtUtil;

    public LoginResponse login(String username, String password) {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new UserNotFoundException("User not found"));

        if (!passwordEncoder.matches(password, user.getPassword())) {
            throw new InvalidCredentialsException("Invalid credentials");
        }

        String accessToken = jwtUtil.generateAccessToken(user);
        RefreshToken refreshToken = refreshTokenService.createRefreshToken(user);

        return new LoginResponse(accessToken, refreshToken.getToken());
    }

    public MessageResponse register(RegisterRequest request) {
        
        if (userRepository.existsByUsername(request.getUsername())) {
            throw new UsernameAlreadyExistException("Username already exists");
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

    public LoginResponse refreshAccessToken(String refreshTokenStr) {
        RefreshToken refreshToken = refreshTokenRepository.findByToken(refreshTokenStr)
                .orElseThrow(() -> new RefreshTokenExpiredException("Invalid refresh token"));

        refreshTokenService.verifyExpiration(refreshToken);
        User user = refreshToken.getUser();

        String newAccessToken = jwtUtil.generateAccessToken(user);
        RefreshToken newRefreshToken = refreshTokenService.createRefreshToken(user);

        return new LoginResponse(newAccessToken, newRefreshToken.getToken());
    }

    public MessageResponse logout(String username) {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new UserNotFoundException("User not found"));

        refreshTokenService.deleteByUser(user);
        return new MessageResponse("User logged out successfully");
    }
}
