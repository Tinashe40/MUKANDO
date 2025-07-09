package com.mukando.authservice.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class RegisterRequest {
    private String username;
    private String password;
    private String role;
    private String email;
    private String firstName;
    private String lastName;
    private String phoneNumber;
    private String address;
    private String city;
    private String country;
}
