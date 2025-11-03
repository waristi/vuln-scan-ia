package com.mercadolibre.vulnscania.domain.port.output;

import com.mercadolibre.vulnscania.domain.model.auth.AccessToken;
import com.mercadolibre.vulnscania.domain.model.auth.RefreshToken;

/**
 * Output Port for token generation.
 * 
 * <p>Defines the contract for generating JWT tokens. Implementations in the
 * infrastructure layer handle the actual JWT creation and signing.</p>
 */
public interface TokenGeneratorPort {
    
    /**
     * Generates an access token for a user.
     * 
     * @param userId The user ID to include in the token
     * @return Access token with expiration (typically 1 hour)
     */
    AccessToken generateAccessToken(String userId);
    
    /**
     * Generates a refresh token for a user.
     * 
     * @param userId The user ID to include in the token
     * @return Refresh token with expiration (typically 8 hours)
     */
    RefreshToken generateRefreshToken(String userId);
    
    /**
     * Validates an access token and extracts user information.
     * 
     * @param token The access token to validate
     * @return User ID if token is valid, null otherwise
     */
    String validateAccessToken(String token);
    
    /**
     * Validates a refresh token and extracts user information.
     * 
     * @param token The refresh token to validate
     * @return User ID if token is valid, null otherwise
     */
    String validateRefreshToken(String token);
}

