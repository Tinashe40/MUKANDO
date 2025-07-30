package com.mukando.authservice.dto;

import java.util.Set;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;

public record RegisterRequest(
    @NotBlank String username,
    @NotBlank String password,
    @Email @NotBlank String email,
    String firstName,
    String lastName,
    @Pattern(regexp = "^\\+?[0-9]{7,15}$") String phoneNumber,
    String address,
    String city,
    String country,
    Set<String> roles
) {}