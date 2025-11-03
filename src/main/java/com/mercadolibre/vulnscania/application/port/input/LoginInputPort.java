package com.mercadolibre.vulnscania.application.port.input;

import com.mercadolibre.vulnscania.domain.command.LoginCommand;

/**
 * Input Port for user login operations.
 * 
 * <p>Defines the interface for login use cases, following hexagonal architecture.
 * Controllers depend on this port rather than concrete use case implementations.</p>
 */
public interface LoginInputPort {
    
    /**
     * Authenticates a user and generates access and refresh tokens.
     * 
     * @param command Login command containing username and password
     * @return Login result with access token, refresh token, and user information
     * @throws com.mercadolibre.vulnscania.domain.exception.InvalidCredentialsException if credentials are invalid
     */
    LoginResult execute(LoginCommand command);
    
    /**
     * Result of login operation.
     * 
     * @param accessToken JWT access token (1 hour expiration)
     * @param refreshToken JWT refresh token (8 hours expiration)
     * @param userId User identifier
     * @param username User's username
     * @param email User's email
     */
    record LoginResult(
        String accessToken,
        String refreshToken,
        String userId,
        String username,
        String email
    ) {}
}

