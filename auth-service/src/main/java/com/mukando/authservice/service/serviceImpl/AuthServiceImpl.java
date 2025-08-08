package com.mukando.authservice.service.serviceImpl;

import java.util.Set;
import java.util.stream.Collectors;

import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.mukando.authservice.dto.LoginRequest;
import com.mukando.authservice.dto.LoginResponse;
import com.mukando.authservice.dto.RegisterRequest;
import com.mukando.authservice.dto.RegisterResponse;
import com.mukando.authservice.dto.UserCreationRequest;
import com.mukando.authservice.dto.UserDetailsResponse;
import com.mukando.authservice.feign.UserServiceClient;
import com.mukando.authservice.model.Role;
import com.mukando.authservice.model.User;
import com.mukando.authservice.repository.UserRepository;
import com.mukando.authservice.security.JwtUtil;
import com.mukando.authservice.service.AuthService;
import com.mukando.commons.exception.EmailAlreadyExistException;
import com.mukando.commons.exception.InvalidCredentialsException;
import com.mukando.commons.exception.UsernameAlreadyExistException;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class AuthServiceImpl implements AuthService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtUtil jwtUtil;
    private final AuthenticationManager authenticationManager;
    private final UserServiceClient userServiceClient;

    @Override
    @Transactional
    public RegisterResponse register(RegisterRequest request) {
        validateRegistration(request);
        User user = createUserFromRequest(request);
        userRepository.save(user);
        
        syncUserToUserService(user);
        return buildRegisterResponse(user);
    }

    @Override
    public boolean verifyPassword(String username, String password) {
        return userRepository.findByUsername(username)
            .map(user -> passwordEncoder.matches(password, user.getPassword()))
            .orElse(false);
    }

    @Override
    @Transactional
    public void updatePassword(String username, String newPassword) {
        userRepository.findByUsername(username)
            .ifPresent(user -> {
                user.setPassword(passwordEncoder.encode(newPassword));
                userRepository.save(user);
            });
    }

    @Override
    public LoginResponse login(LoginRequest request) {
        authenticate(request);
        User user = getUserByUsername(request.username());
        String token = jwtUtil.generateToken(user);
        return buildLoginResponse(user, token);
    }

    @Override
    public UserDetailsResponse getCurrentUser(String authHeader) {
        validateAuthHeader(authHeader);
        String token = authHeader.substring(7);
        return userRepository.findByUsername(jwtUtil.extractUsername(token))
            .map(this::buildUserDetailsResponse)
            .orElseThrow(() -> new InvalidCredentialsException("Invalid token"));
    }

    // Helper methods
    private void validateRegistration(RegisterRequest request) {
        if (userRepository.existsByUsername(request.username())) {
            throw new UsernameAlreadyExistException("Username already taken: " + request.username());
        }
        if (userRepository.existsByEmail(request.email())) {
            throw new EmailAlreadyExistException("Email already registered: " + request.email());
        }
    }

    private User createUserFromRequest(RegisterRequest request) {
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
            .roles(resolveRoles(request))
            .enabled(true)
            .build();
    }

    private Set<Role> resolveRoles(RegisterRequest request) {
        return (request.roles() != null && !request.roles().isEmpty())
            ? request.roles().stream()
                .map(role -> Role.valueOf(role.toUpperCase()))
                .collect(Collectors.toSet())
            : Set.of(Role.USER);
    }

    private void syncUserToUserService(User user) {
        userServiceClient.createUser(new UserCreationRequest(
            user.getUsername(),
            user.getEmail(),
            user.getFirstName(),
            user.getLastName(),
            user.getPhoneNumber(),
            user.getAddress(),
            user.getCity(),
            user.getCountry(),
            user.getRoles().stream()
                .map(Role::name)
                .collect(Collectors.toSet())
        ));
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

    private void authenticate(LoginRequest request) {
        try {
            authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                    request.username(), 
                    request.password()
                )
            );
        } catch (Exception e) {
            throw new InvalidCredentialsException("Invalid credentials");
        }
    }

    private User getUserByUsername(String username) {
        return userRepository.findByUsername(username)
            .orElseThrow(() -> new InvalidCredentialsException("User not found"));
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

    private void validateAuthHeader(String authHeader) {
        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            throw new InvalidCredentialsException("Invalid authorization header");
        }
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