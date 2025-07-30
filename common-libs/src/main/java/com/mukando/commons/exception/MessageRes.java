// MessageRes.java
package com.mukando.commons.exception;

import java.time.LocalDateTime;

import com.fasterxml.jackson.annotation.JsonFormat;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class MessageRes {
    private String message;
    private int status;
    
    @JsonFormat(shape = JsonFormat.Shape.STRING, 
                pattern = "dd/MM/yyyy HH:mm:ss.SSS", 
                timezone = "Africa/Harare")
    private LocalDateTime timestamp;
}