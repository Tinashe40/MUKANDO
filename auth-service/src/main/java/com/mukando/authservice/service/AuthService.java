package com.mukando.authservice.service;

import com.mukando.authservice.dto.LoginRequest;
import com.mukando.authservice.dto.LoginResponse;
import com.mukando.authservice.dto.RegisterRequest;
import com.mukando.authservice.dto.RegisterResponse;
import com.mukando.authservice.model.User;

public interface AuthService {

    /**
     * Registers a new user with the given details.
     *
     * @param registerRequest the registration request
     * @return RegisterResponse (can be enhanced to include message/status)
     */
    RegisterResponse register(RegisterRequest registerRequest);

    /**
     * Logs in a user and returns a token and user details.
     *
     * @param loginRequest the login request containing credentials
     * @return LoginResponse with access token and user info
     */
    LoginResponse login(LoginRequest loginRequest);

    User getCurrentUser(String token);

}
