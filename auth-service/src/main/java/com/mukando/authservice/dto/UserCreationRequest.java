package com.mukando.authservice.dto;

import java.util.Set;

public record UserCreationRequest(
    String username,
    String email,
    String firstName,
    String lastName,
    String phoneNumber,
    String address,
    String city,
    String country,
    Set<String> roles
) {}
