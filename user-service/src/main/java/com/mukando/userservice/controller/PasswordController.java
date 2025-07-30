package com.mukando.userservice.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.mukando.userservice.service.PasswordService;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/password")
@RequiredArgsConstructor
public class PasswordController {

    private final PasswordService passwordService;

    @PostMapping("/forgot")
    public ResponseEntity<Void> forgotPassword(@RequestParam String email) {
        passwordService.initiatePasswordReset(email);
        return ResponseEntity.accepted().build();
    }

    @PostMapping("/reset")
    public ResponseEntity<Void> resetPassword(
            @RequestParam String token,
            @RequestParam String newPassword) {
        passwordService.completePasswordReset(token, newPassword);
        return ResponseEntity.ok().build();
    }
}