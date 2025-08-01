package com.mukando.userservice.service;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.mukando.commons.exception.ResourceNotFoundException;
import com.mukando.commons.exception.TokenExpiredException;
import com.mukando.commons.exception.TokenNotFoundException;
import com.mukando.userservice.model.PasswordResetToken;
import com.mukando.userservice.model.User;
import com.mukando.userservice.repository.PasswordResetTokenRepository;
import com.mukando.userservice.repository.UserRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class PasswordServiceImpl implements PasswordService {

    private final UserRepository userRepository;
    private final PasswordResetTokenRepository tokenRepository;
    private final PasswordEncoder passwordEncoder;
    private final EmailService emailService;

    @Override
    @Transactional
    public void initiatePasswordReset(String email) {
        User user = userRepository.findByEmail(email)
            .orElseThrow(() -> new ResourceNotFoundException("User not found with email: " + email));
        
        // Invalidate any existing tokens
        tokenRepository.deleteByUserId(user.getId());
        
        // Create new token
        PasswordResetToken resetToken = new PasswordResetToken(user);
        tokenRepository.save(resetToken);
        
        // Send email
        emailService.sendPasswordResetEmail(user.getEmail(), resetToken.getToken());
    }

    @Override
    @Transactional
    public void completePasswordReset(String token, String newPassword) {
        PasswordResetToken resetToken = tokenRepository.findByToken(token)
            .orElseThrow(() -> new TokenNotFoundException("Invalid password reset token"));
        
        if (resetToken.isExpired()) {
            tokenRepository.delete(resetToken);
            throw new TokenExpiredException("Password reset token has expired");
        }
        
        User user = resetToken.getUser();
        user.setPassword(passwordEncoder.encode(newPassword));
        userRepository.save(user);
        
        // Cleanup token
        tokenRepository.delete(resetToken);
    }
}