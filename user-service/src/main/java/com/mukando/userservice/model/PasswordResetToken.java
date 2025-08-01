package com.mukando.userservice.model;

import java.time.LocalDateTime;
import java.util.UUID;

import com.mukando.commons.jpa.BaseEntity;

import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "password_reset_tokens")
@NoArgsConstructor
@Data
@AllArgsConstructor
public class PasswordResetToken extends BaseEntity {

    private static final int EXPIRATION_HOURS = 1;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String token;

    @OneToOne(targetEntity = User.class, fetch = FetchType.EAGER)
    @JoinColumn(nullable = false, name = "user_id")
    private User user;

    private LocalDateTime expiryDate;

    public PasswordResetToken(User user) {
        this.user = user;
        this.expiryDate = LocalDateTime.now().plusHours(EXPIRATION_HOURS);
        this.token = UUID.randomUUID().toString();
    }

    public boolean isExpired() {
        return LocalDateTime.now().isAfter(expiryDate);
    }
}