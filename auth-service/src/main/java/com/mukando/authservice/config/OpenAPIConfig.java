package com.mukando.authservice.config;

import java.util.List;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.security.SecurityRequirement;
import io.swagger.v3.oas.models.security.SecurityScheme;

@Configuration
public class OpenAPIConfig {
        @Bean
        public OpenAPI customOpenAPI() {

                return new OpenAPI()
                        .info(new Info()
                        
                                .title("Mukando Auth Service API")
                                .version("1.0")
                                .description("API for authentication, registration, refresh token and logout"))
                        .components(new Components()
                                .addSecuritySchemes("bearerAuth", new SecurityScheme()
                                        .type(SecurityScheme.Type.HTTP)
                                        .scheme("bearer")
                                        .bearerFormat("JWT")))
                        .addSecurityItem(new SecurityRequirement().addList("bearerAuth", List.of()));
        }
}
