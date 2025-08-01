package com.mukando.authservice.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import io.swagger.v3.oas.annotations.enums.SecuritySchemeType;
import io.swagger.v3.oas.annotations.security.SecurityScheme;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;

@Configuration
@SecurityScheme(
        name = "Bearer Authentication",
        description = "Use the JWT token in the format `Bearer <token>`",
        type = SecuritySchemeType.HTTP,
        bearerFormat = "JWT",
        scheme = "bearer"
)
public class OpenAPIConfig {
        @Bean
        public OpenAPI customOpenAPI() {
                return new OpenAPI()
                        .info(new Info()
                                .title("Mukando Auth Service API")
                                .version("1.0")
                                .description("API for authentication, registration, refresh token and logout"));
        }
}
