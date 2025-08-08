package com.mukando.authservice.service.serviceImpl;

import org.springframework.stereotype.Service;

import com.mukando.authservice.model.User;
import com.mukando.authservice.repository.UserRepository;
import com.mukando.authservice.service.UserService;
import com.mukando.commons.exception.NotFoundException;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {
    private final UserRepository userRepository;

    @Override
    public User findByUsername(String username) {
        return userRepository.findByUsername(username)
            .orElseThrow(() -> new NotFoundException("User not found: " + username));
    }
}