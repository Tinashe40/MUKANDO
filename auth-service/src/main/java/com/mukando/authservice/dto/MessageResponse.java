package com.mukando.authservice.dto;

import java.time.LocalDateTime;

import org.hibernate.annotations.UpdateTimestamp;

import com.fasterxml.jackson.annotation.JsonFormat;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class MessageResponse {
    private String message;

    private int status;

    @UpdateTimestamp
    @JsonFormat(shape = JsonFormat.Shape.STRING, locale = "en_ZW", pattern = "yyyy-MM-dd HH:mm:ss", timezone = "Africa/Harare")
    private LocalDateTime timestamp;

    public MessageResponse(String message, int status) {
        this.message = message;
        this.status = status;
    }
}

