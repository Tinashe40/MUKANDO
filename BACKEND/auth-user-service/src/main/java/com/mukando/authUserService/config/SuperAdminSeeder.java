package com.mukando.authUserService.config;

import java.util.Set;

import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.password.PasswordEncoder;

import com.mukando.authUserService.entity.Role;
import com.mukando.authUserService.entity.User;
import com.mukando.authUserService.repository.UserRepository;

import lombok.RequiredArgsConstructor;

@Configuration
@RequiredArgsConstructor
public class SuperAdminSeeder {

    private final PasswordEncoder passwordEncoder;

    @Bean
    CommandLineRunner seedSuperAdmin(UserRepository userRepository) {
        return args -> {
            if (!userRepository.existsByUsername("Tinash")) {
                User user = User.builder()
                        .username("Tinash")
                        .email("tinashe@pesepay.com")
                        .phone("+263772707153")
                        .firstName("Tinashe")
                        .lastName("Mutero")
                        .password(passwordEncoder.encode("tina123"))
                        .roles(Set.of(Role.SUPER_ADMIN))
                        .build();
                userRepository.save(user);
                System.out.println("✅ SUPER_ADMIN seeded: " + user.getFirstName() + " " + user.getLastName());
            }
        };
    }
}
