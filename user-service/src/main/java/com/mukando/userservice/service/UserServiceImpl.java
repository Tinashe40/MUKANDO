package com.mukando.userservice.service;

import java.util.Set;
import java.util.stream.Collectors;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.mukando.commons.exception.EmailAlreadyExistException;
import com.mukando.commons.exception.InvalidCredentialsException;
import com.mukando.commons.exception.ResourceNotFoundException;
import com.mukando.commons.exception.UsernameAlreadyExistException;
import com.mukando.userservice.dto.AuthRegistrationRequest;
import com.mukando.userservice.feign.AuthServiceClient;
import com.mukando.userservice.model.Role;
import com.mukando.userservice.model.User;
import com.mukando.userservice.repository.UserRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {

    private static final String USER_NOT_FOUND = "User not found with ID: ";
    
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final AuthServiceClient authServiceClient;

    @Override
    @Transactional
    public User createUser(User user) {
        validateUserDoesNotExist(user);
        encodeUserPassword(user);
        User createdUser = userRepository.save(user);
        syncUserToAuthService(createdUser);
        return createdUser;
    }

    @Override
    @Transactional
    public void changePassword(Long userId, String currentPassword, String newPassword) {
        User user = getUserById(userId);
        verifyCurrentPassword(user, currentPassword);
        updatePasswordAndSync(user, newPassword);
    }

    @Override
    @Transactional
    public User updateUser(Long id, User updatedUser) {
        User existingUser = getUserById(id);
        updateEmailIfChanged(existingUser, updatedUser);
        updateUserDetails(existingUser, updatedUser);
        return userRepository.save(existingUser);
    }

    @Override
    @Transactional
    public void deleteUser(Long id) {
        User user = getUserById(id);
        userRepository.delete(user);
    }

    @Override
    public User getUserById(Long id) {
        return userRepository.findById(id)
            .orElseThrow(() -> new ResourceNotFoundException(USER_NOT_FOUND + id));
    }

    @Override
    public Page<User> getAllUsers(Pageable pageable) {
        return userRepository.findAll(pageable);
    }

    @Override
    @Transactional
    public User assignRoles(Long userId, Set<String> roles) {
        User user = getUserById(userId);
        user.setRoles(convertToRoleSet(roles));
        return userRepository.save(user);
    }

    @Override
    @Transactional
    public User updateUserStatus(Long userId, boolean enabled) {
        User user = getUserById(userId);
        user.setEnabled(enabled);
        return userRepository.save(user);
    }

    @Override
    public User findByUsername(String username) {
        return userRepository.findByUsername(username)
            .orElseThrow(() -> new ResourceNotFoundException("User not found with username: " + username));
    }

    @Override
    public User findByEmail(String email) {
        return userRepository.findByEmail(email)
            .orElseThrow(() -> new ResourceNotFoundException("User not found with email: " + email));
    }

    @Override
    public boolean existsByUsername(String username) {
        return userRepository.existsByUsername(username);
    }

    @Override
    public boolean existsByEmail(String email) {
        return userRepository.existsByEmail(email);
    }

    @Override
    public Set<String> getUserRoles(Long userId) {
        return getUserById(userId).getRoles().stream()
            .map(Role::name)
            .collect(Collectors.toSet());
    }

    @Override
    public boolean isUserEnabled(Long userId) {
        return getUserById(userId).isEnabled();
    }

    @Override
    public boolean isUserAdmin(Long userId) {
        Set<Role> roles = getUserById(userId).getRoles();
        return roles.contains(Role.ADMIN) || roles.contains(Role.SUPERADMIN);
    }

    @Override
    @Transactional
    public User internalCreateUser(User user) {
        validateUserDoesNotExist(user);
        return userRepository.save(user);
    }

    // Helper methods
    private void validateUserDoesNotExist(User user) {
        if (existsByUsername(user.getUsername())) {
            throw new UsernameAlreadyExistException(
                "Username '" + user.getUsername() + "' already exists"
            );
        }
        
        if (existsByEmail(user.getEmail())) {
            throw new EmailAlreadyExistException(
                "Email '" + user.getEmail() + "' already registered"
            );
        }
    }

    private void encodeUserPassword(User user) {
        user.setPassword(passwordEncoder.encode(user.getPassword()));
    }

    private void verifyCurrentPassword(User user, String currentPassword) {
        if (!passwordEncoder.matches(currentPassword, user.getPassword())) {
            throw new InvalidCredentialsException("Current password is incorrect");
        }
    }

    private void updatePasswordAndSync(User user, String newPassword) {
        String encodedPassword = passwordEncoder.encode(newPassword);
        user.setPassword(encodedPassword);
        userRepository.save(user);
        authServiceClient.updatePassword(user.getUsername(), encodedPassword);
    }

    private void updateEmailIfChanged(User existing, User updated) {
        if (!existing.getEmail().equals(updated.getEmail())) {
            if (existsByEmail(updated.getEmail())) {
                throw new EmailAlreadyExistException(
                    "Email '" + updated.getEmail() + "' already in use"
                );
            }
            existing.setEmail(updated.getEmail());
        }
    }

    private void updateUserDetails(User existing, User updated) {
        existing.setFirstName(updated.getFirstName());
        existing.setLastName(updated.getLastName());
        existing.setPhoneNumber(updated.getPhoneNumber());
        existing.setAddress(updated.getAddress());
        existing.setCity(updated.getCity());
        existing.setCountry(updated.getCountry());
    }

    private Set<Role> convertToRoleSet(Set<String> roles) {
        return roles.stream()
            .map(role -> Role.valueOf(role.toUpperCase()))
            .collect(Collectors.toSet());
    }

    private void syncUserToAuthService(User user) {
        authServiceClient.registerUser(new AuthRegistrationRequest(
            user.getUsername(),
            user.getPassword(), // Already encoded
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
}