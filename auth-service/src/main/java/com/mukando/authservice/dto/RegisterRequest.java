package com.mukando.authservice.dto;

import com.mukando.authservice.model.Role;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import lombok.Data;

@Data
public class RegisterRequest {

    @NotBlank(message="Username should be provided")
    @Schema(description = "Unique username", example = "tinashe123")
    private String username;

    @NotBlank(message="Password should be provided")
    @Schema(description = "Password", example = "securePass123!")
    private String password;

    
    private Role role;

    @Email
    @NotBlank(message="Email should be provided")
    @Schema(description = "Valid email", example = "tinashe@email.com")
    private String email;

    @Schema(example = "Tinashe")
    private String firstName;

    @Schema(example = "Mutero")
    private String lastName;

    @Pattern(regexp = "^\\+?[0-9]{7,15}$")
    @Schema(description = "Phone number", example = "+263772707153")
    private String phoneNumber;

    @Schema(example = "1531 Munhende St.")
    private String address;

    @Schema(example = "Mpandawana")
    private String city;

    @Schema(example = "Zimbabwe")
    private String country;
}
