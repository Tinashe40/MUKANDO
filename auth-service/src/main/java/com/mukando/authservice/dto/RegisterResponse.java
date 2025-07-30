package com.mukando.authservice.dto;

import java.util.Set;

import com.mukando.authservice.model.Role;

public record RegisterResponse(
    String username,
    String email,
    String message,
    Set<Role> roles,
    String firstName,
    String lastName,
    String phoneNumber,
    String address,
    String city,
    String country
) {}