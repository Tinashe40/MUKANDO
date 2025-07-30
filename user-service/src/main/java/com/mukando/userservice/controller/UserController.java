package com.mukando.userservice.controller;

import java.util.Set;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.mukando.userservice.model.User;
import com.mukando.userservice.service.UserService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/users")
@RequiredArgsConstructor
@Tag(name = "User Management", description = "Endpoints for managing user accounts and permissions")
@SecurityRequirement(name = "Bearer Authentication")
public class UserController {

    private final UserService userService;

    @Operation(
        summary = "Create new user",
        description = "Create a new user account. Requires ADMIN role."
    )
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "User created successfully",
            content = @Content(mediaType = "application/json", 
            schema = @Schema(implementation = User.class))),
        @ApiResponse(responseCode = "400", description = "Invalid input data"),
        @ApiResponse(responseCode = "403", description = "Insufficient permissions"),
        @ApiResponse(responseCode = "409", description = "Username or email already exists"),
        @ApiResponse(responseCode = "401", description = "Authentication required")
    })
    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<User> createUser(@RequestBody User user) {
        return ResponseEntity.ok(userService.createUser(user));
    }

    @Operation(
        summary = "Update user details",
        description = "Update user information. Users can update their own profile, admins can update any profile."
    )
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "User updated successfully",
            content = @Content(mediaType = "application/json", 
            schema = @Schema(implementation = User.class))),
        @ApiResponse(responseCode = "404", description = "User not found"),
        @ApiResponse(responseCode = "403", description = "Insufficient permissions"),
        @ApiResponse(responseCode = "400", description = "Invalid input data"),
        @ApiResponse(responseCode = "409", description = "Email already in use")
    })
    @PutMapping("/{id}")
    @PreAuthorize("#id == authentication.principal.id or hasRole('ADMIN')")
    public ResponseEntity<User> updateUser(
        @Parameter(description = "ID of the user to update", required = true)
        @PathVariable Long id, 
        @RequestBody User user) {
        return ResponseEntity.ok(userService.updateUser(id, user));
    }

    @Operation(
        summary = "Delete user account",
        description = "Permanently delete a user account. Requires ADMIN role."
    )
    @ApiResponses({
        @ApiResponse(responseCode = "204", description = "User deleted successfully"),
        @ApiResponse(responseCode = "404", description = "User not found"),
        @ApiResponse(responseCode = "403", description = "Insufficient permissions"),
        @ApiResponse(responseCode = "401", description = "Authentication required")
    })
    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Void> deleteUser(
        @Parameter(description = "ID of the user to delete", required = true)
        @PathVariable Long id) {
        userService.deleteUser(id);
        return ResponseEntity.noContent().build();
    }

    @Operation(
        summary = "Get user by ID",
        description = "Retrieve user details by ID. Users can view their own profile, admins can view any profile."
    )
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "User details retrieved",
            content = @Content(mediaType = "application/json", 
            schema = @Schema(implementation = User.class))),
        @ApiResponse(responseCode = "404", description = "User not found"),
        @ApiResponse(responseCode = "403", description = "Insufficient permissions"),
        @ApiResponse(responseCode = "401", description = "Authentication required")
    })
    @GetMapping("/{id}")
    @PreAuthorize("#id == authentication.principal.id or hasRole('ADMIN')")
    public ResponseEntity<User> getUserById(
        @Parameter(description = "ID of the user to retrieve", required = true)
        @PathVariable Long id) {
        return ResponseEntity.ok(userService.getUserById(id));
    }

    @Operation(
        summary = "List all users",
        description = "Get paginated list of all users. Requires ADMIN role."
    )
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "User list retrieved",
            content = @Content(mediaType = "application/json", 
            schema = @Schema(implementation = Page.class))),
        @ApiResponse(responseCode = "403", description = "Insufficient permissions"),
        @ApiResponse(responseCode = "401", description = "Authentication required")
    })
    @GetMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Page<User>> getAllUsers(Pageable pageable) {
        return ResponseEntity.ok(userService.getAllUsers(pageable));
    }

    @Operation(
        summary = "Assign user roles",
        description = "Assign roles to a user. Requires ADMIN role."
    )
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Roles assigned successfully",
            content = @Content(mediaType = "application/json", 
            schema = @Schema(implementation = User.class))),
        @ApiResponse(responseCode = "404", description = "User not found"),
        @ApiResponse(responseCode = "403", description = "Insufficient permissions"),
        @ApiResponse(responseCode = "400", description = "Invalid role names"),
        @ApiResponse(responseCode = "401", description = "Authentication required")
    })
    @PutMapping("/{id}/roles")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<User> assignRoles(
        @Parameter(description = "ID of the user to assign roles to", required = true)
        @PathVariable Long id, 
        @RequestBody Set<String> roles) {
        return ResponseEntity.ok(userService.assignRoles(id, roles));
    }

    @Operation(
        summary = "Update user status",
        description = "Activate or deactivate a user account. Requires ADMIN role."
    )
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Status updated successfully",
            content = @Content(mediaType = "application/json", 
            schema = @Schema(implementation = User.class))),
        @ApiResponse(responseCode = "404", description = "User not found"),
        @ApiResponse(responseCode = "403", description = "Insufficient permissions"),
        @ApiResponse(responseCode = "401", description = "Authentication required")
    })
    @PutMapping("/{id}/status")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<User> updateUserStatus(
        @Parameter(description = "ID of the user to update", required = true)
        @PathVariable Long id, 
        @Parameter(description = "New account status (true = active, false = inactive)", required = true)
        @RequestParam boolean enabled) {
        return ResponseEntity.ok(userService.updateUserStatus(id, enabled));
    }

    @Operation(
        summary = "Change password",
        description = "Change user password. Users can change their own password."
    )
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Password changed successfully"),
        @ApiResponse(responseCode = "404", description = "User not found"),
        @ApiResponse(responseCode = "403", description = "Insufficient permissions"),
        @ApiResponse(responseCode = "401", description = "Authentication required")
    })
    @PutMapping("/{id}/password")
    @PreAuthorize("#id == authentication.principal.id")
    public ResponseEntity<Void> changePassword(
        @Parameter(description = "ID of the user changing password", required = true)
        @PathVariable Long id,
        @Parameter(description = "New password value", required = true)
        @RequestBody String newPassword) {
        userService.changePassword(id, newPassword);
        return ResponseEntity.ok().build();
    }

    @Operation(
        summary = "Find user by username",
        description = "Retrieve user details by username. Requires ADMIN role."
    )
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "User found",
            content = @Content(mediaType = "application/json", 
            schema = @Schema(implementation = User.class))),
        @ApiResponse(responseCode = "404", description = "User not found"),
        @ApiResponse(responseCode = "403", description = "Insufficient permissions"),
        @ApiResponse(responseCode = "401", description = "Authentication required")
    })
    @GetMapping("/username/{username}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<User> findByUsername(
        @Parameter(description = "Username to search for", required = true)
        @PathVariable String username) {
        return ResponseEntity.ok(userService.findByUsername(username));
    }

    @Operation(
        summary = "Find user by email",
        description = "Retrieve user details by email. Requires ADMIN role."
    )
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "User found",
            content = @Content(mediaType = "application/json", 
            schema = @Schema(implementation = User.class))),
        @ApiResponse(responseCode = "404", description = "User not found"),
        @ApiResponse(responseCode = "403", description = "Insufficient permissions"),
        @ApiResponse(responseCode = "401", description = "Authentication required")
    })
    @GetMapping("/email/{email}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<User> findByEmail(
        @Parameter(description = "Email address to search for", required = true)
        @PathVariable String email) {
        return ResponseEntity.ok(userService.findByEmail(email));
    }

    @Operation(
        summary = "Get user roles",
        description = "Retrieve roles assigned to a user. Users can view their own roles, admins can view any roles."
    )
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Roles retrieved",
            content = @Content(mediaType = "application/json", 
            schema = @Schema(implementation = Set.class))),
        @ApiResponse(responseCode = "404", description = "User not found"),
        @ApiResponse(responseCode = "403", description = "Insufficient permissions"),
        @ApiResponse(responseCode = "401", description = "Authentication required")
    })
    @GetMapping("/{id}/roles")
    @PreAuthorize("#id == authentication.principal.id or hasRole('ADMIN')")
    public ResponseEntity<Set<String>> getUserRoles(
        @Parameter(description = "ID of the user to get roles for", required = true)
        @PathVariable Long id) {
        return ResponseEntity.ok(userService.getUserRoles(id));
    }

    @Operation(
        summary = "Check user status",
        description = "Check if a user account is enabled. Users can check their own status, admins can check any status."
    )
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Status retrieved",
            content = @Content(mediaType = "application/json", 
            schema = @Schema(type = "boolean"))),
        @ApiResponse(responseCode = "404", description = "User not found"),
        @ApiResponse(responseCode = "403", description = "Insufficient permissions"),
        @ApiResponse(responseCode = "401", description = "Authentication required")
    })
    @GetMapping("/{id}/enabled")
    @PreAuthorize("#id == authentication.principal.id or hasRole('ADMIN')")
    public ResponseEntity<Boolean> isUserEnabled(
        @Parameter(description = "ID of the user to check", required = true)
        @PathVariable Long id) {
        return ResponseEntity.ok(userService.isUserEnabled(id));
    }

    @Operation(
        summary = "Check admin status",
        description = "Check if a user has admin privileges. Users can check their own status, admins can check any status."
    )
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Admin status retrieved",
            content = @Content(mediaType = "application/json", 
            schema = @Schema(type = "boolean"))),
        @ApiResponse(responseCode = "404", description = "User not found"),
        @ApiResponse(responseCode = "403", description = "Insufficient permissions"),
        @ApiResponse(responseCode = "401", description = "Authentication required")
    })
    @GetMapping("/{id}/admin")
    @PreAuthorize("#id == authentication.principal.id or hasRole('ADMIN')")
    public ResponseEntity<Boolean> isUserAdmin(
        @Parameter(description = "ID of the user to check", required = true)
        @PathVariable Long id) {
        return ResponseEntity.ok(userService.isUserAdmin(id));
    }
}
