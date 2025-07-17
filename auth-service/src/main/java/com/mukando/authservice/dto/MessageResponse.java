package com.mukando.authservice.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import jakarta.persistence.Column;
import lombok.AllArgsConstructor;
import lombok.Data;
import org.hibernate.annotations.UpdateTimestamp;
import org.springframework.web.bind.annotation.ResponseStatus;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
public class MessageResponse {
    private String message;

    private int status;

    @UpdateTimestamp
    @JsonFormat(shape = JsonFormat.Shape.STRING, locale = "en_ZW", pattern = "dd/MM/yyyy HH:mm:sss", timezone = "Africa/Harare")
    private LocalDateTime timestamp;

}
