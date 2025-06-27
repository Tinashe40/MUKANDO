package com.mukando.authUserService.exceptions;

public class InvalidRoleOperationException extends RuntimeException {
    public InvalidRoleOperationException(String message) {
        super(message);
    }
}