package com.mukando.authservice.service;

import com.mukando.authservice.model.User;

public interface UserService {
    User findByUsername(String username);
}