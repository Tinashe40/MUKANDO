package com.mukando.authservice.controller;

import java.util.Map;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CookieValue;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.mukando.authservice.dto.LoginRequest;
import com.mukando.authservice.dto.LogoutRequest;
import com.mukando.authservice.dto.RegisterRequest;
import com.mukando.authservice.service.AuthService;

import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.ws.rs.core.HttpHeaders;
import lombok.Data;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
public class AuthController {

    @Data
    public static class RefreshTokenRequest {
        private String refreshToken;
    }

    private final AuthService authService;

    @PostMapping("/register")
    public ResponseEntity<String> register(@RequestBody RegisterRequest request) {
        String message = authService.register(
                request.getUsername(),
                request.getPassword(),
                request.getRole()
        );
        return ResponseEntity.ok(message);
    }

    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody LoginRequest request, HttpServletResponse response) {
        Map<String, String> tokens = authService.login(request.getUsername(), request.getPassword());

        Cookie cookie = new Cookie("refreshToken", tokens.get("refreshToken"));
        cookie.setHttpOnly(true);
        cookie.setPath("/auth");
        cookie.setMaxAge(7 * 24 * 60 * 60); // 7 days

        response.addCookie(cookie);
        return ResponseEntity.ok(Map.of("accessToken", tokens.get("accessToken")));
    }

    @PostMapping("/refresh-token")
    public ResponseEntity<?> refreshToken(@CookieValue("refreshToken") String refreshToken) {
        Map<String, String> tokens = authService.refreshAccessToken(refreshToken);

        Cookie cookie = new Cookie("refreshToken", tokens.get("refreshToken"));
        cookie.setHttpOnly(true);
        cookie.setPath("/auth");
        cookie.setMaxAge(7 * 24 * 60 * 60); 

        return ResponseEntity.ok()
                .header(HttpHeaders.SET_COOKIE, cookie.toString())
                .body(Map.of("accessToken", tokens.get("accessToken")));
    }

    @PostMapping("/logout")
    public ResponseEntity<String> logout(@RequestBody LogoutRequest request) {
        return ResponseEntity.ok(authService.logout(request.getUsername()));
    }
}
