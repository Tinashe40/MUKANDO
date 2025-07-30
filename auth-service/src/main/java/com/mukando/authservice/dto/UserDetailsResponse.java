package com.mukando.authservice.dto;

import java.util.Set;

import com.mukando.authservice.model.Role;

public record UserDetailsResponse(
    String username,
    String email,
    String firstName,
    String lastName,
    String phoneNumber,
    String address,
    String city,
    String country,
    Set<Role> roles
) {}