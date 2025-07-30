package com.mukando.userservice.service;

import java.util.Set;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import com.mukando.userservice.model.User;

public interface UserService {
    User createUser(User user);
    User updateUser(Long id, User user);
    void deleteUser(Long id);
    User getUserById(Long id);
    Page<User> getAllUsers(Pageable pageable);
    User assignRoles(Long userId, Set<String> roles);
    User updateUserStatus(Long userId, boolean enabled);
    void changePassword(Long userId, String newPassword);
    User findByUsername(String username);
    User findByEmail(String email);
    boolean existsByUsername(String username);
    boolean existsByEmail(String email);
    Set<String> getUserRoles(Long userId);
    boolean isUserEnabled(Long userId);
    boolean isUserAdmin(Long userId);
}