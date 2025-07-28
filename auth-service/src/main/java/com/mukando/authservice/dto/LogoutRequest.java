package com.mukando.authservice.dto;

import lombok.Data;

@Data
public class LogoutRequest {
    private String username;
    private String token;
}
