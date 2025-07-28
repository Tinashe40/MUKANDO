package com.mukando.authservice.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.mukando.authservice.dto.LoginRequest;
import com.mukando.authservice.dto.LoginResponse;
import com.mukando.authservice.dto.RegisterRequest;
import com.mukando.authservice.dto.RegisterResponse;
import com.mukando.authservice.model.User;
import com.mukando.authservice.service.AuthService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;


@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
@Tag(name = "Authentication", description = "Endpoints for user authentication, registration, token management, and logout")
public class AuthController {

    private final AuthService authService;

    @PostMapping("/register")
    @Operation(summary = "Register a new user")
    public ResponseEntity<RegisterResponse> register(@Valid @RequestBody RegisterRequest request) {
        RegisterResponse response = authService.register(request);
        return ResponseEntity.ok(response);
    }

    @PostMapping("/login")
    @Operation(summary = "Authenticate a user and issue a token")
    public ResponseEntity<LoginResponse> login(@Valid @RequestBody LoginRequest request) {
        LoginResponse response = authService.login(request);
        return ResponseEntity.ok(response);
    }

    // @PostMapping("/logout")
    // @Operation(summary = "Logout the user and invalidate their token")
    // public ResponseEntity<String> logout(@Valid @RequestBody LogoutRequest request, HttpServletRequest servletRequest) {
    //     authService.logout(request.getToken(), servletRequest);
    //     return ResponseEntity.ok("Logged out successfully");
    // }

    @GetMapping("/current-user")
    @Operation(summary = "Get details of the currently authenticated user")
    public ResponseEntity<LoginResponse> getCurrentUser(HttpServletRequest request) {
        String token = request.getHeader("Authorization");
        if (token != null && token.startsWith("Bearer ")) {
            token = token.substring(7);
            User userDetails = authService.getCurrentUser(token);
            return ResponseEntity.ok(userDetails != null ? 
                new LoginResponse(
                    userDetails.getUsername(),
                    userDetails.getEmail(),
                    "Your Profile Details",
                    userDetails.getRole().name(),
                    userDetails.getFirstName(),
                    userDetails.getPhoneNumber(),
                    token
                ) : null);
        }
        return ResponseEntity.badRequest().build();
    }
}
