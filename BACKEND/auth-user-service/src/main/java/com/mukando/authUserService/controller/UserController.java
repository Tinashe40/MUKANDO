package com.mukando.authUserService.controller;

import java.util.List;
import java.util.Set;

import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.mukando.authUserService.entity.Role;
import com.mukando.authUserService.entity.User;
import com.mukando.authUserService.exceptions.InvalidRoleOperationException;
import com.mukando.authUserService.service.UserService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/users")
@RequiredArgsConstructor
@Tag(name = "User Management", description = "Admin & SuperAdmin User Operations")
public class UserController {

    private final UserService userService;

    @GetMapping("/all")
    @PreAuthorize("hasAnyAuthority('ADMIN', 'SUPER_ADMIN')")
    @Operation(summary = "Get all users")
    public ResponseEntity<List<User>> getAllUsers() {
        return ResponseEntity.ok(userService.getAllUsers());
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasAnyAuthority('ADMIN', 'SUPER_ADMIN')")
    @Operation(summary = "Get user by ID")
    public ResponseEntity<User> getUser(@PathVariable Long id) {
        return ResponseEntity.ok(userService.getById(id));
    }

    @PutMapping("/{id}/promote")
    @PreAuthorize("hasAnyAuthority('ADMIN', 'SUPER_ADMIN')")
    @Operation(summary = "Promote user to a role (e.g., ADMIN, TREASURER)")
    public ResponseEntity<User> promote(
            @PathVariable Long id,
            @RequestParam Role role,
            Authentication authentication
    ) {
        // Prevent self-promotion
        User currentUser = userService.findByUsername(authentication.getName());
        if (currentUser.getId().equals(id)) {
            throw new InvalidRoleOperationException("You cannot promote yourself");
        }
        
        return ResponseEntity.ok(userService.promoteUser(id, role));
    }

    @PutMapping("/{id}/demote")
    @PreAuthorize("hasAuthority('SUPER_ADMIN')")
    @Operation(summary = "Demote user from a role (e.g., ADMIN, TREASURER)")
    public ResponseEntity<User> demote(
            @PathVariable Long id,
            @RequestParam Role role,
            Authentication authentication
    ) {
        User currentUser = userService.findByUsername(authentication.getName());
        if (currentUser.getId().equals(id)) {
            throw new InvalidRoleOperationException("You cannot demote yourself");
        }
        
        return ResponseEntity.ok(userService.demoteUser(id, role));
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasAuthority('SUPER_ADMIN')")
    @Operation(summary = "Delete a user (SuperAdmin only)")
    public ResponseEntity<String> delete(
            @PathVariable Long id,
            Authentication authentication
    ) {
        User currentUser = userService.findByUsername(authentication.getName());
        if (currentUser.getId().equals(id)) {
            throw new InvalidRoleOperationException("You cannot delete yourself");
        }
        
        userService.deleteUser(id);
        return ResponseEntity.ok("User deleted successfully");
    }

    @GetMapping("/roles")
    @Operation(summary = "Get all available roles")
    public ResponseEntity<Set<Role>> roles() {
        return ResponseEntity.ok(userService.getAllRoles());
    }
}
