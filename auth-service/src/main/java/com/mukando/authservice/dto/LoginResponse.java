package com.mukando.authservice.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class LoginResponse {
    private String username;
    private String email;
    private String message;
    private String role;
    private String firstName;
    private String phoneNumber;
    private String token;
}
