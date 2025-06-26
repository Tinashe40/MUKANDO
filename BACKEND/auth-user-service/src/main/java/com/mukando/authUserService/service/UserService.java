package com.mukando.authUserService.service;

import java.util.EnumSet;
import java.util.List;
import java.util.Set;

import org.springframework.stereotype.Service;

import com.mukando.authUserService.entity.Role;
import com.mukando.authUserService.entity.User;
import com.mukando.authUserService.repository.UserRepository;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;

    public List<User> getAllUsers() {
        return userRepository.findAll();
    }

    public User getById(Long id) {
        return userRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("User not found"));
    }

    @Transactional
    public void promoteUser(Long id, Role newRole) {
        User user = getById(id);
        user.getRoles().add(newRole);
        userRepository.save(user);
    }

    @Transactional
    public void demoteUser(Long id, Role roleToRemove) {
        User user = getById(id);
        user.getRoles().remove(roleToRemove);
        userRepository.save(user);
    }

    @Transactional
    public void deleteUser(Long id) {
        userRepository.deleteById(id);
    }

    public Set<Role> getAllRoles() {
        return EnumSet.allOf(Role.class);
    }
}
