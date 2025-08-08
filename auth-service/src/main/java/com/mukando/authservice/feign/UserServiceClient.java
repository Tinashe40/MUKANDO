// UserServiceClient.java
package com.mukando.authservice.feign;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

import com.mukando.authservice.dto.UserCreationRequest;

@FeignClient(name = "user-service", path = "/api/users/internal")
public interface UserServiceClient {
    @PostMapping
    void createUser(@RequestBody UserCreationRequest request);
}