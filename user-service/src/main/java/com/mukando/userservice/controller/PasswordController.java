package com.mukando.userservice.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.mukando.userservice.service.PasswordService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/password")
@RequiredArgsConstructor
@Tag(name = "Password Management", description = "Endpoints for password reset operations")
public class PasswordController {

    private final PasswordService passwordService;

    @Operation(
        summary = "Initiate password reset",
        description = "Request a password reset link to be sent to your email"
    )
    @ApiResponses({
        @ApiResponse(responseCode = "202", description = "Password reset initiated"),
        @ApiResponse(responseCode = "404", description = "User not found")
    })
    @PostMapping("/forgot")
    public ResponseEntity<Void> forgotPassword(
        @Parameter(description = "User's registered email address", required = true)
        @RequestParam String email) {
        passwordService.initiatePasswordReset(email);
        return ResponseEntity.accepted().build();
    }

    @Operation(
        summary = "Complete password reset",
        description = "Reset password using a valid token from your email"
    )
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Password reset successful"),
        @ApiResponse(responseCode = "400", description = "Invalid or expired token"),
        @ApiResponse(responseCode = "404", description = "Token not found")
    })
    @PostMapping("/reset")
    public ResponseEntity<Void> resetPassword(
        @Parameter(description = "Password reset token received via email", required = true)
        @RequestParam String token,
        @Parameter(description = "New password for the account", required = true)
        @RequestParam String newPassword) {
        passwordService.completePasswordReset(token, newPassword);
        return ResponseEntity.ok().build();
    }
}