package com.mukando.authservice.service;

import com.mukando.authservice.model.User;
import org.springframework.stereotype.Service;

@Service
public interface UserService {

    User findByUsername(String username);

}
