package com.mukando.commons.exception;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.AllArgsConstructor;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
public class MessageRes {
    private String message;

    private int status;

    @JsonFormat(shape = JsonFormat.Shape.STRING, locale = "en_ZW", pattern = "dd/MM/yyyy HH:mm:sss", timezone = "Africa/Harare")
    private LocalDateTime timestamp;

}
