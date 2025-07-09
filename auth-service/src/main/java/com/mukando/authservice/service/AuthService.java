package com.mukando.authservice.service;

import java.util.Map;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

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

    public Map<String, String> login(String username, String password) {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("User not found"));

        if (!passwordEncoder.matches(password, user.getPassword())) {
            throw new RuntimeException("Invalid credentials");
        }

        String accessToken = jwtUtil.generateAccessToken(user);
        RefreshToken refreshToken = refreshTokenService.createRefreshToken(user);

        return Map.of(
                "accessToken", accessToken,
                "refreshToken", refreshToken.getToken()
        );
    }

    public String register(String username, String password, String roleStr) {
        if (userRepository.existsByUsername(username)) {
            throw new RuntimeException("Username already exists");
        }

        Role role = Role.valueOf(roleStr.toUpperCase());

        User user = User.builder()
                .username(username)
                .password(passwordEncoder.encode(password))
                .role(role)
                .build();

        userRepository.save(user);
        return "User registered successfully";
    }

    public Map<String, String> refreshAccessToken(String refreshTokenStr) {
        RefreshToken refreshToken = refreshTokenRepository.findByToken(refreshTokenStr)
                .orElseThrow(() -> new RuntimeException("Invalid refresh token"));

        refreshTokenService.verifyExpiration(refreshToken);
        User user = refreshToken.getUser();

        String newAccessToken = jwtUtil.generateAccessToken(user);
        RefreshToken newRefreshToken = refreshTokenService.createRefreshToken(user);

        return Map.of(
                "accessToken", newAccessToken,
                "refreshToken", newRefreshToken.getToken()
        );
    }

    public String logout(String username) {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("User not found"));

        refreshTokenService.deleteByUser(user);
        return "User logged out successfully";
    }
}
