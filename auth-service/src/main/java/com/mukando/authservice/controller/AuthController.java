package com.mukando.authservice.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.mukando.authservice.dto.LoginRequest;
import com.mukando.authservice.dto.LoginResponse;
import com.mukando.authservice.dto.RegisterRequest;
import com.mukando.authservice.dto.RegisterResponse;
import com.mukando.authservice.dto.UpdatePasswordRequest;
import com.mukando.authservice.dto.UserDetailsResponse;
import com.mukando.authservice.service.AuthService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
@Tag(name = "Authentication", description = "User authentication and registration endpoints")
public class AuthController {

    private final AuthService authService;

    @Operation(
        summary = "Register new user",
        description = "Creates a new user account with the provided details"
    )
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "User registered successfully",
            content = @Content(mediaType = "application/json", 
            schema = @Schema(implementation = RegisterResponse.class))),
        @ApiResponse(responseCode = "400", description = "Invalid input"),
        @ApiResponse(responseCode = "409", description = "Username/email conflict"),
        @ApiResponse(responseCode = "500", description = "Internal error")
    })
    @PostMapping("/register")
    public ResponseEntity<RegisterResponse> register(@Valid @RequestBody RegisterRequest request) {
        return ResponseEntity.ok(authService.register(request));
    }

    @Operation(
        summary = "Verify password (internal)",
        description = "Internal endpoint to verify user credentials"
    )
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Verification result",
            content = @Content(mediaType = "application/json", 
            schema = @Schema(implementation = Boolean.class))),
        @ApiResponse(responseCode = "500", description = "Internal error")
    })
    @PostMapping("/internal/verify-password")
    public ResponseEntity<Boolean> verifyPassword(
        @RequestParam String username, 
        @RequestParam String password) {
        return ResponseEntity.ok(authService.verifyPassword(username, password));
    }
    
    @Operation(
        summary = "Update password (internal)",
        description = "Internal endpoint to update user password"
    )
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Password updated"),
        @ApiResponse(responseCode = "500", description = "Internal error")
    })
    @PutMapping("/internal/update-password")
    public ResponseEntity<Void> updatePassword(
        @RequestParam String username,
        @RequestBody UpdatePasswordRequest request) {
        authService.updatePassword(username, request.newPassword());
        return ResponseEntity.ok().build();
    }

    @Operation(
        summary = "Authenticate user",
        description = "Authenticates user credentials and returns JWT token"
    )
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Authentication successful",
            content = @Content(mediaType = "application/json", 
            schema = @Schema(implementation = LoginResponse.class))),
        @ApiResponse(responseCode = "401", description = "Invalid credentials"),
        @ApiResponse(responseCode = "500", description = "Internal error")
    })
    @PostMapping("/login")
    public ResponseEntity<LoginResponse> login(@Valid @RequestBody LoginRequest request) {
        return ResponseEntity.ok(authService.login(request));
    }

    @Operation(
        summary = "Get current user",
        description = "Returns details of the authenticated user"
    )
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "User details retrieved",
            content = @Content(mediaType = "application/json", 
            schema = @Schema(implementation = UserDetailsResponse.class))),
        @ApiResponse(responseCode = "401", description = "Unauthorized"),
        @ApiResponse(responseCode = "500", description = "Internal error")
    })
    @GetMapping("/current-user")
    @SecurityRequirement(name = "Bearer Authentication")
    public ResponseEntity<UserDetailsResponse> getCurrentUser(
        @RequestHeader("Authorization") String authHeader) {
        return ResponseEntity.ok(authService.getCurrentUser(authHeader));
    }
}
