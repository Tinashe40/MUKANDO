package com.mukando.authservice.service;

import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.mukando.authservice.dto.LoginRequest;
import com.mukando.authservice.dto.LoginResponse;
import com.mukando.authservice.dto.RegisterRequest;
import com.mukando.authservice.dto.RegisterResponse;
import com.mukando.authservice.dto.UserDetailsResponse;
import com.mukando.authservice.model.Role;
import com.mukando.authservice.model.User;
import com.mukando.authservice.repository.UserRepository;
import com.mukando.authservice.security.JwtUtil;
import com.mukando.commons.exception.EmailAlreadyExistException;
import com.mukando.commons.exception.InvalidCredentialsException;
import com.mukando.commons.exception.ResourceNotFoundException;
import com.mukando.commons.exception.UsernameAlreadyExistException;

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
        validateRegistrationRequest(request);
        User user = buildUserFromRequest(request);
        userRepository.save(user);
        return buildRegisterResponse(user);
    }

    @Override
    public LoginResponse login(LoginRequest request) {
        authenticateUser(request);
        User user = getUserByUsername(request.username());
        String token = jwtUtil.generateToken(user);
        return buildLoginResponse(user, token);
    }

    @Override
    public UserDetailsResponse getCurrentUser(String authHeader) {
        validateAuthorizationHeader(authHeader);
        String token = authHeader.substring(7);
        return getUserDetailsResponse(token);
    }

    private void validateRegistrationRequest(RegisterRequest request) {
        if (userRepository.existsByUsername(request.username())) {
            throw new UsernameAlreadyExistException(
                "Username '" + request.username() + "' is already taken"
            );
        }
        if (userRepository.existsByEmail(request.email())) {
            throw new EmailAlreadyExistException(
                "Email '" + request.email() + "' is already registered"
            );
        }
    }

    private User buildUserFromRequest(RegisterRequest request) {
        return User.builder()
            .username(request.username())
            .email(request.email())
            .password(passwordEncoder.encode(request.password()))
            .firstName(request.firstName())
            .lastName(request.lastName())
            .phoneNumber(request.phoneNumber())
            .address(request.address())
            .city(request.city())
            .country(request.country())
            .roles(resolveUserRoles(request))
            .enabled(true)
            .build();
    }

    private Set<Role> resolveUserRoles(RegisterRequest request) {
        return Optional.ofNullable(request.roles())
            .filter(roles -> !roles.isEmpty())
            .map(roles -> roles.stream()
                .map(role -> Role.valueOf(role.toUpperCase()))
                .collect(Collectors.toSet()))
            .orElse(Set.of(Role.USER));
    }

    private RegisterResponse buildRegisterResponse(User user) {
        return new RegisterResponse(
            user.getUsername(),
            user.getEmail(),
            "Registration successful",
            user.getRoles(),
            user.getFirstName(),
            user.getLastName(),
            user.getPhoneNumber(),
            user.getAddress(),
            user.getCity(),
            user.getCountry()
        );
    }

    private void authenticateUser(LoginRequest request) {
        try {
            authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                    request.username(), 
                    request.password()
                )
            );
        } catch (AuthenticationException e) {
            throw new InvalidCredentialsException("Invalid username or password");
        }
    }

    private User getUserByUsername(String username) {
        return userRepository.findByUsername(username)
            .orElseThrow(() -> new ResourceNotFoundException("User not found"));
    }

    private LoginResponse buildLoginResponse(User user, String token) {
        return new LoginResponse(
            user.getUsername(),
            user.getEmail(),
            "Login successful",
            user.getRoles(),
            user.getFirstName(),
            user.getPhoneNumber(),
            token
        );
    }

    private void validateAuthorizationHeader(String authHeader) {
        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            throw new InvalidCredentialsException("Missing or invalid Authorization header");
        }
    }

    private UserDetailsResponse getUserDetailsResponse(String token) {
        String username = jwtUtil.extractUsername(token);
        return userRepository.findByUsername(username)
            .map(this::buildUserDetailsResponse)
            .orElseThrow(() -> new ResourceNotFoundException("User not found"));
    }

    private UserDetailsResponse buildUserDetailsResponse(User user) {
        return new UserDetailsResponse(
            user.getUsername(),
            user.getEmail(),
            user.getFirstName(),
            user.getLastName(),
            user.getPhoneNumber(),
            user.getAddress(),
            user.getCity(),
            user.getCountry(),
            user.getRoles()
        );
    }
}