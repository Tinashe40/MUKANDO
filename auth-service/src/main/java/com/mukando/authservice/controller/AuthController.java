package com.mukando.authservice.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.ErrorResponse;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.mukando.authservice.dto.LoginRequest;
import com.mukando.authservice.dto.LoginResponse;
import com.mukando.authservice.dto.RegisterRequest;
import com.mukando.authservice.dto.RegisterResponse;
import com.mukando.authservice.dto.UserDetailsResponse;
import com.mukando.authservice.service.AuthService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
@Tag(name = "Authentication", description = "User authentication and registration endpoints")
@SecurityRequirement(name = "Bearer Authentication")
public class AuthController {

    private final AuthService authService;

    @Operation(
        summary = "Register a  new user",
        description = "Registers a new user with the provided details. "
            + "Returns a success message and the registered user's details."
    )
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "User registered successfully",
            content = @Content(mediaType="application/json",
                schema = @Schema(implementation = RegisterResponse.class))),

        @ApiResponse(responseCode = "400", description = "Invalid input data",
            content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
        @ApiResponse(responseCode = "409", description = "User already exists"),
        @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    @PostMapping("/register")
    public ResponseEntity<RegisterResponse> register(@Valid @RequestBody RegisterRequest request) {
        return ResponseEntity.ok(authService.register(request));
    }

    @PostMapping("/login")
    @Operation(summary = "Authenticate user and get access token",
        description = "Authenticates a user with the provided credentials and returns an access token."
    )
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "User authenticated successfully",
            content = @Content(mediaType="application/json",
                schema = @Schema(implementation = LoginResponse.class))),
        @ApiResponse(responseCode = "401", description = "Invalid credentials"),
        @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    public ResponseEntity<LoginResponse> login(@Valid @RequestBody LoginRequest request) {
        return ResponseEntity.ok(authService.login(request));
    }

    @GetMapping("/current-user")
    @Operation(summary = "Get authenticated user details",
        description = "Retrieves the details of the currently authenticated user."
    )
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "User details retrieved successfully",
            content = @Content(mediaType="application/json",
            schema = @Schema(implementation = UserDetailsResponse.class))),
        @ApiResponse(responseCode = "401", description = "User not authenticated"),
        @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    public ResponseEntity<UserDetailsResponse> getCurrentUser(HttpServletRequest request) {
        return ResponseEntity.ok(authService.getCurrentUser(
            request.getHeader("Authorization")
        ));
    }
}