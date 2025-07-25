package com.mukando.commons.exception;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;

@ControllerAdvice(annotations = {RestController.class, Controller.class})
public class GlobalExceptionHandler {

    @ExceptionHandler(AccessDeniedException.class)
    public ResponseEntity<MessageRes> handleAccessDeniedException(AccessDeniedException ex) {
        return buildResponse(ex.getMessage(), HttpStatus.FORBIDDEN);
    }

    @ExceptionHandler(BadRequestException.class)
    public ResponseEntity<MessageRes> handleBadRequestException(BadRequestException ex) {
        return buildResponse(ex.getMessage(), HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(InvalidCredentialsException.class)
    public ResponseEntity<MessageRes> handleInvalidCredentialsException(InvalidCredentialsException ex) {
        return buildResponse(ex.getMessage(), HttpStatus.UNAUTHORIZED);
    }

    @ExceptionHandler(NotFoundException.class)
    public ResponseEntity<MessageRes> handleNotFoundException(NotFoundException ex) {
        return buildResponse(ex.getMessage(), HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler(UserNotFoundException.class)
    public ResponseEntity<MessageRes> handleUserNotFoundException(UserNotFoundException ex) {
        return buildResponse(ex.getMessage(), HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler(UsernameAlreadyExistException.class)
    public ResponseEntity<MessageRes> handleUsernameAlreadyExistException(UsernameAlreadyExistException ex) {
        return buildResponse(ex.getMessage(), HttpStatus.CONFLICT); // Changed from NOT_FOUND to CONFLICT
    }

    @ExceptionHandler(ValidationException.class)
    public ResponseEntity<MessageRes> handleValidationException(ValidationException ex) {
        return buildResponse(ex.getMessage(), HttpStatus.CONFLICT);
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<MessageRes> handleGenericException(Exception ex) {
        return buildResponse("An unexpected error occurred: " + ex.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
    }

    private ResponseEntity<MessageRes> buildResponse(String message, HttpStatus status) {
        MessageRes error = new MessageRes(
                message,
                status.value(),
                LocalDateTime.now()
        );
        return new ResponseEntity<>(error, status);
    }
}
