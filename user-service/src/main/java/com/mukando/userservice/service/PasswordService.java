package com.mukando.userservice.service;

public interface PasswordService {
    void initiatePasswordReset(String email);
    void completePasswordReset(String token, String newPassword);
}