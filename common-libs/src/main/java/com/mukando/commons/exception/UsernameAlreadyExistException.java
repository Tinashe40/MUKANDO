package com.mukando.commons.exception;

public class UsernameAlreadyExistException extends RuntimeException {
    public UsernameAlreadyExistException(String message) {
        super(message);
    }
}
