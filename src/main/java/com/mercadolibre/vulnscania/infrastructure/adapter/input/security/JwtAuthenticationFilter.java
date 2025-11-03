package com.mercadolibre.vulnscania.infrastructure.adapter.input.security;

import com.mercadolibre.vulnscania.domain.port.output.TokenGeneratorPort;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

/**
 * Security filter that validates JWT access tokens for protected endpoints.
 * 
 * <p>This filter intercepts HTTP requests and validates the JWT token in the
 * Authorization header. Public endpoints (login, refresh, health) are excluded
 * from authentication.</p>
 * 
 * <p><strong>Token Format</strong>: Bearer {access_token}</p>
 * 
 * <p><strong>Note</strong>: This filter is not annotated with @Component because
 * it's created and configured directly in SecurityConfiguration to avoid duplicate
 * filter registration.</p>
 */
public class JwtAuthenticationFilter extends OncePerRequestFilter {
    
    private static final Logger log = LoggerFactory.getLogger(JwtAuthenticationFilter.class);
    
    private static final String AUTHORIZATION_HEADER = "Authorization";
    private static final String BEARER_PREFIX = "Bearer ";
    
    private static final List<String> PUBLIC_ENDPOINTS = Arrays.asList(
        "/api/v1/auth/register",
        "/api/v1/auth/login",
        "/api/v1/auth/refresh",
        "/actuator/health",
        "/swagger-ui",
        "/v3/api-docs"
    );
    
    private final TokenGeneratorPort tokenValidator;
    
    public JwtAuthenticationFilter(TokenGeneratorPort tokenValidator) {
        this.tokenValidator = tokenValidator;
    }
    
    @Override
    protected void doFilterInternal(
            HttpServletRequest request,
            HttpServletResponse response,
            FilterChain filterChain) throws ServletException, IOException {
        
        String requestPath = request.getRequestURI();
        
        // Skip validation for public endpoints
        if (isPublicEndpoint(requestPath)) {
            filterChain.doFilter(request, response);
            return;
        }
        
        String authorizationHeader = request.getHeader(AUTHORIZATION_HEADER);
        
        if (authorizationHeader == null || !authorizationHeader.startsWith(BEARER_PREFIX)) {
            log.warn("Missing or invalid Authorization header for request to: {}", requestPath);
            sendUnauthorizedResponse(response, "Missing or invalid Authorization header. Use format: Bearer {token}");
            return;
        }
        
        // Extract token
        String token = authorizationHeader.substring(BEARER_PREFIX.length()).trim();
        
        // Validate access token
        String userId = tokenValidator.validateAccessToken(token);
        if (userId == null) {
            log.warn("Invalid or expired access token for request to: {}", requestPath);
            sendUnauthorizedResponse(response, "Invalid or expired access token");
            return;
        }
        
        // Token is valid, create Authentication object and set it in SecurityContext
        // This tells Spring Security that the request is authenticated
        Authentication authentication = new UsernamePasswordAuthenticationToken(
            userId,
            null,
            Collections.singletonList(new SimpleGrantedAuthority("ROLE_USER"))
        );
        
        SecurityContextHolder.getContext().setAuthentication(authentication);
        
        // Add user ID to request for downstream use (optional, can also get from SecurityContext)
        request.setAttribute("userId", userId);
        
        // Continue with request
        filterChain.doFilter(request, response);
    }
    
    private boolean isPublicEndpoint(String path) {
        return PUBLIC_ENDPOINTS.stream().anyMatch(path::startsWith);
    }
    
    private void sendUnauthorizedResponse(HttpServletResponse response, String message) throws IOException {
        response.setStatus(HttpStatus.UNAUTHORIZED.value());
        response.setContentType("application/json");
        // Use generic message to avoid information disclosure
        response.getWriter().write(
            "{\"error\":\"Unauthorized\",\"message\":\"Invalid or missing authentication token\"}"
        );
    }
}

