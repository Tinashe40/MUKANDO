package com.mukando.authservice.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CookieValue;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.mukando.authservice.dto.AuthResponse;
import com.mukando.authservice.dto.LoginRequest;
import com.mukando.authservice.dto.LogoutRequest;
import com.mukando.authservice.dto.MessageResponse;
import com.mukando.authservice.dto.RegisterRequest;
import com.mukando.authservice.service.AuthService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
@Tag(name = "Authentication", description = "Endpoints for user authentication, registration, token management and logout")
public class AuthController {

    private final AuthService authService;

    @PostMapping("/register")
    @Operation(summary = "Register new user")
    public ResponseEntity<MessageResponse> register(@RequestBody RegisterRequest request) {
        return ResponseEntity.ok(authService.register(request));
    }

    @PostMapping("/login")
    @Operation(summary = "Login and receive access and refresh tokens")
    public ResponseEntity<AuthResponse> login(@Valid @RequestBody LoginRequest request, HttpServletResponse response) {
        AuthResponse tokens = authService.login(request.getUsername(), request.getPassword());

        Cookie cookie = new Cookie("refreshToken", tokens.getRefreshToken());
        cookie.setHttpOnly(true);
        cookie.setPath("/auth");
        cookie.setMaxAge(7 * 24 * 60 * 60); // 7 days
        cookie.setSecure(true);

        response.addCookie(cookie);
        return ResponseEntity.ok(tokens);
    }

    @PostMapping("/refresh-token")
    @Operation(summary = "Refresh access token using refresh token")
    public ResponseEntity<AuthResponse> refreshToken(@CookieValue("refreshToken") String refreshToken, HttpServletResponse response) {
        AuthResponse tokens = authService.refreshAccessToken(refreshToken);

        Cookie cookie = new Cookie("refreshToken", tokens.getRefreshToken());
        cookie.setHttpOnly(true);
        cookie.setPath("/auth");
        cookie.setMaxAge(7 * 24 * 60 * 60);
        cookie.setSecure(true); 

        response.addCookie(cookie);
        return ResponseEntity.ok(tokens);
    }

    @PostMapping("/logout")
    @Operation(summary = "Logout user and invalidate tokens")
    public ResponseEntity<MessageResponse> logout(@Valid @RequestBody LogoutRequest request) {
        MessageResponse response = authService.logout(request.getUsername());
        return ResponseEntity.ok(response);
    }
}
