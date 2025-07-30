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
@Tag(name = "Password Management", description = "Password reset operations")
public class PasswordController {

    private final PasswordService passwordService;

    @Operation(
        summary = "Initiate password reset",
        description = "Sends a password reset link to the user's email"
    )
    @ApiResponses(value = {
        @ApiResponse(responseCode = "202", description = "Reset initiated"),
        @ApiResponse(responseCode = "404", description = "User not found")
    })
    @PostMapping("/forgot")
    public ResponseEntity<Void> forgotPassword(
        @Parameter(description = "User's email address") 
        @RequestParam String email) {
        passwordService.initiatePasswordReset(email);
        return ResponseEntity.accepted().build();
    }

    @Operation(
        summary = "Complete password reset",
        description = "Resets password using a valid token"
    )
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Password reset"),
        @ApiResponse(responseCode = "400", description = "Invalid token")
    })
    @PostMapping("/reset")
    public ResponseEntity<Void> resetPassword(
        @Parameter(description = "Password reset token") 
        @RequestParam String token,
        @Parameter(description = "New password") 
        @RequestParam String newPassword) {
        passwordService.completePasswordReset(token, newPassword);
        return ResponseEntity.ok().build();
    }
}