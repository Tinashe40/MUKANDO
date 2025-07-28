package com.mukando.authservice.service;

import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.mukando.authservice.dto.LoginRequest;
import com.mukando.authservice.dto.LoginResponse;
import com.mukando.authservice.dto.RegisterRequest;
import com.mukando.authservice.dto.RegisterResponse;
import com.mukando.authservice.model.Role;
import com.mukando.authservice.model.User;
import com.mukando.authservice.repository.UserRepository;
import com.mukando.commons.exception.BadRequestException;
import com.mukando.commons.exception.ResourceNotFoundException;
import com.mukando.authservice.security.JwtUtil;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class AuthServiceImpl implements AuthService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtUtil jwtUtil;
    private final AuthenticationManager authenticationManager;
    @Override
    @Transactional
    public RegisterResponse register(RegisterRequest request) {
        if (userRepository.existsByUsername(request.getUsername())) {
            throw new BadRequestException("Username already exists.");
        }

        if (userRepository.existsByEmail(request.getEmail())) {
            throw new BadRequestException("Email is already in use.");
        }

        User user = User.builder()
                .username(request.getUsername())
                .email(request.getEmail())
                .password(passwordEncoder.encode(request.getPassword()))
                .firstName(request.getFirstName())
                .lastName(request.getLastName())
                .phoneNumber(request.getPhoneNumber())
                .address(request.getAddress())
                .city(request.getCity())
                .country(request.getCountry())
                .role(Role.USER)
                .build();

        userRepository.save(user);

        return new RegisterResponse(
                user.getUsername(),
                user.getEmail(),
                "Registration successful",
                user.getRole().name(),
                user.getFirstName(),
                user.getLastName(),
                user.getPhoneNumber(),
                user.getAddress(),
                user.getCity(),
                user.getCountry()
        );
    }

    @Override
    public LoginResponse login(LoginRequest request) {
        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(request.getUsername(), request.getPassword())
        );

        User user = userRepository.findByUsername(request.getUsername())
                .orElseThrow(() -> new ResourceNotFoundException("User not found."));

        String token = jwtUtil.generateToken((UserDetails) user, user.getId(), user.getRole().name());

        return new LoginResponse(
                user.getUsername(),
                user.getEmail(),
                "Login successful",
                user.getPhoneNumber(),
                user.getAddress(),
                user.getRole().name(),
                token
        );
    }

    @Override
    public User getCurrentUser(String token) {
        String username = jwtUtil.extractUsername(token);
        return userRepository.findByUsername(username)
                .orElseThrow(() -> new ResourceNotFoundException("No user is authenticated."));
    }
}
