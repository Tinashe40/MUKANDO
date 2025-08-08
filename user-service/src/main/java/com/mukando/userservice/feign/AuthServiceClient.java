package com.mukando.userservice.feign;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;

import com.mukando.userservice.dto.AuthRegistrationRequest;

@FeignClient(name = "auth-service")
public interface AuthServiceClient {

    @PostMapping("/auth/internal/register")
    void registerUser(@RequestBody AuthRegistrationRequest request);

    @PutMapping("/auth/internal/password")
    void updatePassword(@RequestParam String username, @RequestBody String newPassword);
}
