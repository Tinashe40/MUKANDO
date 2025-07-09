package com.mukando.apigateway.filter;

import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.core.Ordered;
import org.springframework.http.HttpStatus;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.MalformedJwtException;
import io.jsonwebtoken.UnsupportedJwtException;
import io.jsonwebtoken.security.SecurityException;
import lombok.extern.slf4j.Slf4j;
import reactor.core.publisher.Mono;

@Slf4j
@Component
public class JwtAuthFilter implements GlobalFilter, Ordered {

    @Value("${jwt.secret}")
    private String jwtSecret;

    // Define protected route access per role
    private final Map<String, List<String>> roleAccessMap = Map.of(
        "/admin", List.of("SUPERADMIN", "ADMIN"),
        "/groups", List.of("SUPERADMIN", "ADMIN", "TREASURER"),
        "/contributions", List.of("SUPERADMIN", "TREASURER", "MEMBER"),
        "/loans", List.of("SUPERADMIN", "ADMIN", "TREASURER", "MEMBER"),
        "/reports", List.of("SUPERADMIN", "ADMIN", "TREASURER"),
        "/users", List.of("SUPERADMIN", "ADMIN"),
        "/notifications", List.of("SUPERADMIN", "ADMIN")
    );

    @Override
    public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {
        ServerHttpRequest request = exchange.getRequest();
        String path = request.getPath().toString();

        // Allow unauthenticated access to auth endpoints
        if (path.startsWith("/auth")) {
            return chain.filter(exchange);
        }

        if (!request.getHeaders().containsKey("Authorization")) {
            log.warn("Missing Authorization header for request to {}", path);
            exchange.getResponse().setStatusCode(HttpStatus.UNAUTHORIZED);
            return exchange.getResponse().setComplete();
        }

        String token = request.getHeaders().getFirst("Authorization");
        if (token == null || !token.startsWith("Bearer ")) {
            log.warn("Invalid Authorization header format for request to {}", path);
            exchange.getResponse().setStatusCode(HttpStatus.UNAUTHORIZED);
            return exchange.getResponse().setComplete();
        }

        token = token.substring(7);

        try {
            Claims claims = Jwts.parserBuilder()
                    .setSigningKey(jwtSecret.getBytes(StandardCharsets.UTF_8))
                    .build()
                    .parseClaimsJws(token)
                    .getBody();

            String userId = claims.get("userId", String.class);
            String username = claims.get("username", String.class);
            String role = claims.get("role", String.class);

            if (userId == null || username == null || role == null) {
                log.warn("JWT claims missing required fields for request to {}", path);
                exchange.getResponse().setStatusCode(HttpStatus.UNAUTHORIZED);
                return exchange.getResponse().setComplete();
            }

            // Role-Based Path Blocking
            for (Map.Entry<String, List<String>> entry : roleAccessMap.entrySet()) {
                if (path.startsWith(entry.getKey())) {
                    if (!entry.getValue().contains(role)) {
                        log.warn("Access denied for user '{}' with role '{}' to path {}", username, role, path);
                        exchange.getResponse().setStatusCode(HttpStatus.FORBIDDEN);
                        return exchange.getResponse().setComplete();
                    }
                }
            }

            // Forward user info downstream
            ServerHttpRequest modifiedRequest = request.mutate()
                    .header("X-User-Id", userId)
                    .header("X-Username", username)
                    .header("X-User-Role", role)
                    .build();

            return chain.filter(exchange.mutate().request(modifiedRequest).build());

        } catch (ExpiredJwtException e) {
            log.warn("JWT token expired: {}", e.getMessage());
            exchange.getResponse().setStatusCode(HttpStatus.UNAUTHORIZED);
        } catch (MalformedJwtException | UnsupportedJwtException | SecurityException e) {
            log.warn("Invalid JWT token: {}", e.getMessage());
            exchange.getResponse().setStatusCode(HttpStatus.UNAUTHORIZED);
        } catch (Exception e) {
            log.error("Unexpected error parsing JWT token: {}", e.getMessage());
            exchange.getResponse().setStatusCode(HttpStatus.UNAUTHORIZED);
        }

        return exchange.getResponse().setComplete();
    }

    @Override
    public int getOrder() {
        return -1; // Ensure this filter runs early
    }
}
