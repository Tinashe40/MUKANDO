package com.mukando.authUserService.service;

import java.util.EnumSet;
import java.util.List;
import java.util.Set;

import org.springframework.stereotype.Service;

import com.mukando.authUserService.entity.Role;
import com.mukando.authUserService.entity.User;
import com.mukando.authUserService.exceptions.InvalidRoleOperationException;
import com.mukando.authUserService.exceptions.UserNotFoundException;
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
                .orElseThrow(() -> new UserNotFoundException("User not found with id: " + id));
    }
    
    public User findByUsername(String username) {
        return userRepository.findByUsername(username)
                .orElseThrow(() -> new UserNotFoundException("User not found: " + username));
    }

    @Transactional
    public User promoteUser(Long id, Role newRole) {
        User user = getById(id);
        
        
        if (user.getRoles().contains(newRole)) {
            throw new InvalidRoleOperationException("User already has role: " + newRole);
        }
        
        user.getRoles().add(newRole);
        return userRepository.save(user);
    }

    @Transactional
    public User demoteUser(Long id, Role roleToRemove) {
        User user = getById(id);
        
        if (!user.getRoles().contains(roleToRemove)) {
            throw new InvalidRoleOperationException("User does not have role: " + roleToRemove);
        }
        
        if (user.getRoles().size() == 1) {
            throw new InvalidRoleOperationException("Cannot remove user's last role");
        }
        
        user.getRoles().remove(roleToRemove);
        return userRepository.save(user);
    }

    @Transactional
    public void deleteUser(Long id) {
        if (!userRepository.existsById(id)) {
            throw new UserNotFoundException("User not found with id: " + id);
        }
        userRepository.deleteById(id);
    }

    public Set<Role> getAllRoles() {
        return EnumSet.allOf(Role.class);
    }
}
