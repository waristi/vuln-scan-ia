package com.mercadolibre.vulnscania.domain.command;

import java.util.Objects;

/**
 * Command to refresh an access token using a refresh token.
 * 
 * <p>Immutable record following the Command Pattern.
 * Represents the user's intention to obtain a new access token.</p>
 * 
 * @param refreshToken The refresh token to use for generating a new access token
 */
public record RefreshTokenCommand(String refreshToken) {
    
    public RefreshTokenCommand {
        Objects.requireNonNull(refreshToken, "Refresh token is required");
        
        if (refreshToken.isBlank()) {
            throw new IllegalArgumentException("Refresh token cannot be blank");
        }
    }
}

