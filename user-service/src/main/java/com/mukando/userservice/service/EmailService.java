package com.mukando.userservice.service;

public interface EmailService {
    void sendPasswordResetEmail(String email, String resetLink);
}