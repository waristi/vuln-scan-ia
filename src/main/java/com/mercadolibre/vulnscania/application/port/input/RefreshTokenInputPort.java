package com.mercadolibre.vulnscania.application.port.input;

import com.mercadolibre.vulnscania.domain.command.RefreshTokenCommand;

/**
 * Input Port for token refresh operations.
 * 
 * <p>Defines the interface for refresh token use cases, following hexagonal architecture.
 * Controllers depend on this port rather than concrete use case implementations.</p>
 */
public interface RefreshTokenInputPort {
    
    /**
     * Refreshes an access token using a refresh token.
     * 
     * @param command Refresh token command containing the refresh token
     * @return Token refresh result with new access token, refresh token, and user information
     * @throws com.mercadolibre.vulnscania.domain.exception.InvalidTokenException if refresh token is invalid or expired
     */
    TokenRefreshResult execute(RefreshTokenCommand command);
    
    /**
     * Result of token refresh operation.
     * 
     * @param accessToken New JWT access token (1 hour expiration)
     * @param refreshToken New JWT refresh token (8 hours expiration)
     * @param userId User identifier
     * @param username User's username
     * @param email User's email
     */
    record TokenRefreshResult(
        String accessToken,
        String refreshToken,
        String userId,
        String username,
        String email
    ) {}
}

