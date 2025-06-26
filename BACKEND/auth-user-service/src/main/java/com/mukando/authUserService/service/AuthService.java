package com.mukando.authUserService.service;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.mukando.authUserService.dto.AuthResponse;
import com.mukando.authUserService.dto.LoginRequest;
import com.mukando.authUserService.dto.RegisterRequest;
import com.mukando.authUserService.entity.Role;
import com.mukando.authUserService.entity.User;
import com.mukando.authUserService.repository.UserRepository;
import com.mukando.authUserService.security.JwtService;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;
    private final AuthenticationManager authenticationManager;

    public AuthResponse register(RegisterRequest request) {
        if (userRepository.existsByUsername(request.getUsername()))
            throw new RuntimeException("Username already taken");
        if (userRepository.existsByEmail(request.getEmail()))
            throw new RuntimeException("Email already taken");

        Set<Role> roles = new HashSet<>();
        if (request.getRoles() != null) {
            for (String r : request.getRoles()) {
                roles.add(Role.valueOf(r.toUpperCase()));
            }
        } else {
            roles.add(Role.MEMBER); // default role
        }

        User user = User.builder()
                .username(request.getUsername())
                .email(request.getEmail())
                .phone(request.getPhone())
                .password(passwordEncoder.encode(request.getPassword()))
                .roles(roles)
                .build();

        userRepository.save(user);

        Map<String, Object> claims = new HashMap<>();
        claims.put("username", user.getUsername());
        claims.put("roles", String.join(",", roles.stream().map(Enum::name).toList()));

        String token = jwtService.generateToken(claims, user.getUsername());

        return AuthResponse.builder()
                .token(token)
                .username(user.getUsername())
                .email(user.getEmail())
                .role(roles.stream().findFirst().orElse(Role.MEMBER).name())
                .build();
    }

    public AuthResponse login(LoginRequest request) {
        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        request.getUsername(),
                        request.getPassword()
                )
        );

        User user = userRepository.findByUsername(request.getUsername())
                .orElseThrow(() -> new RuntimeException("User not found"));

        Map<String, Object> claims = new HashMap<>();
        claims.put("username", user.getUsername());
        claims.put("roles", String.join(",", user.getRoles().stream().map(Enum::name).toList()));

        String token = jwtService.generateToken(claims, user.getUsername());

        return AuthResponse.builder()
                .token(token)
                .username(user.getUsername())
                .email(user.getEmail())
                .role(user.getRoles().stream().findFirst().orElse(Role.MEMBER).name())
                .build();
    }

    public User getUserProfile(String username) {
        return userRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("User not found"));
    }
}
