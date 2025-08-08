package com.mukando.userservice.dto;

import java.util.Set;

public record AuthRegistrationRequest(
    String username,
    String password,
    String email,
    String firstName,
    String lastName,
    String phoneNumber,
    String address,
    String city,
    String country,
    Set<String> roles
) {}