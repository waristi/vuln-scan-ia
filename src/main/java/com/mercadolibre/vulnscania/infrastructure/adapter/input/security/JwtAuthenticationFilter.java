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
 *
 * @author Bernardo Zuluaga
 * @since 1.0.0
 */
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private static final Logger log = LoggerFactory.getLogger(JwtAuthenticationFilter.class);

    /**
     * HTTP Authorization header name.
     */
    private static final String AUTHORIZATION_HEADER = "Authorization";

    /**
     * Bearer token prefix.
     */
    private static final String BEARER_PREFIX = "Bearer ";

    /**
     * List of public endpoints that don't require authentication.
     */
    private static final List<String> PUBLIC_ENDPOINTS = Arrays.asList(
        "/api/v1/auth/register",
        "/api/v1/auth/login",
        "/api/v1/auth/refresh",
        "/actuator/health",
        "/swagger-ui",
        "/v3/api-docs"
    );

    private final TokenGeneratorPort tokenValidator;

    /**
     * Constructs a new JwtAuthenticationFilter.
     *
     * @param tokenValidator the token validator port for validating JWT tokens
     */
    public JwtAuthenticationFilter(TokenGeneratorPort tokenValidator) {
        this.tokenValidator = tokenValidator;
    }

    /**
     * Filters incoming requests and validates JWT tokens.
     *
     * <p>Public endpoints bypass authentication. Protected endpoints require
     * a valid JWT token in the Authorization header. Valid tokens result in
     * authentication being set in the Spring Security context.</p>
     *
     * @param request the HTTP servlet request
     * @param response the HTTP servlet response
     * @param filterChain the filter chain
     * @throws ServletException if a servlet error occurs
     * @throws IOException if an I/O error occurs
     */
    @Override
    protected void doFilterInternal(
            HttpServletRequest request,
            HttpServletResponse response,
            FilterChain filterChain) throws ServletException, IOException {

        String requestPath = request.getRequestURI();

        if (isPublicEndpoint(requestPath)) {
            filterChain.doFilter(request, response);
            return;
        }

        String authorizationHeader = request.getHeader(AUTHORIZATION_HEADER);

        if (authorizationHeader == null || !authorizationHeader.startsWith(BEARER_PREFIX)) {
            log.warn("Missing or invalid Authorization header for request to: {}", requestPath);
            sendUnauthorizedResponse(response);
            return;
        }

        String token = authorizationHeader.substring(BEARER_PREFIX.length()).trim();

        String userId = tokenValidator.validateAccessToken(token);
        if (userId == null) {
            log.warn("Invalid or expired access token for request to: {}", requestPath);
            sendUnauthorizedResponse(response);
            return;
        }

        Authentication authentication = new UsernamePasswordAuthenticationToken(
            userId,
            null,
            Collections.singletonList(new SimpleGrantedAuthority("ROLE_USER"))
        );

        SecurityContextHolder.getContext().setAuthentication(authentication);

        request.setAttribute("userId", userId);

        filterChain.doFilter(request, response);
    }

    /**
     * Checks if the given path is a public endpoint.
     *
     * @param path the request path to check
     * @return true if the path is a public endpoint that doesn't require authentication
     */
    private boolean isPublicEndpoint(String path) {
        return PUBLIC_ENDPOINTS.stream().anyMatch(path::startsWith);
    }

    /**
     * Sends an unauthorized response with a generic error message.
     *
     * <p>Uses a generic message to avoid information disclosure about
     * authentication failures.</p>
     *
     * @param response the HTTP servlet response
     * @throws IOException if an I/O error occurs while writing the response
     */
    private void sendUnauthorizedResponse(HttpServletResponse response) throws IOException {
        response.setStatus(HttpStatus.UNAUTHORIZED.value());
        response.setContentType("application/json");
        response.getWriter().write(
            "{\"error\":\"Unauthorized\",\"message\":\"Invalid or missing authentication token\"}"
        );
    }
}

