package com.mukando.userservice.service;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

@Service
public class EmailServiceImpl implements EmailService {

    private final JavaMailSender mailSender;
    
    @Value("${app.email.from}")
    private String fromEmail;
    
    @Value("${app.base-url}")
    private String baseUrl;

    public EmailServiceImpl(JavaMailSender mailSender) {
        this.mailSender = mailSender;
    }

    @Override
    public void sendPasswordResetEmail(String email, String token) {
        String resetLink = baseUrl + "/reset-password?token=" + token;
        String subject = "Password Reset Request";
        String message = "To reset your password, click the link below:\n" + resetLink + 
                         "\n\nThis link will expire in 1 hour.";

        SimpleMailMessage mailMessage = new SimpleMailMessage();
        mailMessage.setFrom(fromEmail);
        mailMessage.setTo(email);
        mailMessage.setSubject(subject);
        mailMessage.setText(message);
        
        mailSender.send(mailMessage);
    }
}