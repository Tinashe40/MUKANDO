package com.mukando.apigateway.exception;

import java.util.HashMap;
import java.util.Map;

import org.springframework.boot.web.reactive.error.ErrorWebExceptionHandler;
import org.springframework.core.annotation.Order;
import org.springframework.core.io.buffer.DataBufferFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import io.jsonwebtoken.JwtException;
import reactor.core.publisher.Mono;

@Component
@Order(-2)
public class GlobalExceptionHandler implements ErrorWebExceptionHandler {

    private final ObjectMapper objectMapper = new ObjectMapper();

    @Override
    public @NonNull Mono<Void> handle(@NonNull ServerWebExchange exchange, @NonNull Throwable ex) {
        HttpStatus status;
        String error;
        String message = ex.getMessage();

        if (ex instanceof JwtException) {
            status = HttpStatus.UNAUTHORIZED;
            error = "Unauthorized";
        } else if (ex instanceof NotFoundException) {
            status = HttpStatus.NOT_FOUND;
            error = "Not Found";
        } else if (ex instanceof BadRequestException) {
            status = HttpStatus.BAD_REQUEST;
            error = "Bad Request";
        } else if (ex instanceof ValidationException) {
            status = HttpStatus.UNPROCESSABLE_ENTITY;
            error = "Validation Failed";
        } else if (ex instanceof IllegalArgumentException) {
            status = HttpStatus.BAD_REQUEST;
            error = "Illegal Argument";
        } else if (ex instanceof IllegalStateException) {
            status = HttpStatus.INTERNAL_SERVER_ERROR;
            error = "Illegal State";
        } else if (ex instanceof RuntimeException) {
            status = HttpStatus.INTERNAL_SERVER_ERROR;
            error = "Internal Server Error";
        } else if (ex instanceof Exception) {
            status = HttpStatus.INTERNAL_SERVER_ERROR;
            error = "Internal Server Error";
        } else {
            status = HttpStatus.INTERNAL_SERVER_ERROR;
            error = "Internal Server Error";
        }

        exchange.getResponse().setStatusCode(status);
        exchange.getResponse().getHeaders().setContentType(MediaType.APPLICATION_JSON);

        Map<String, Object> errorAttributes = new HashMap<>();
        errorAttributes.put("status", status.value());
        errorAttributes.put("error", error);
        errorAttributes.put("message", message);

        return exchange.getResponse().writeWith(Mono.fromSupplier(() -> {
            DataBufferFactory bufferFactory = exchange.getResponse().bufferFactory();
            try {
                byte[] bytes = objectMapper.writeValueAsBytes(errorAttributes);
                return bufferFactory.wrap(bytes);
            } catch (JsonProcessingException e) {
                return bufferFactory.wrap(new byte[0]);
            }
        }));
    }
}
