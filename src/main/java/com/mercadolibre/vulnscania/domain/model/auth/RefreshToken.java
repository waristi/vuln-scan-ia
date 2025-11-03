package com.mercadolibre.vulnscania.domain.model.auth;

import java.time.Instant;
import java.util.Objects;

/**
 * Value Object representing a refresh token.
 * 
 * <p>Refresh tokens are long-lived (typically 8 hours) and used to obtain new
 * access tokens without requiring user credentials. They should be stored securely
 * and revoked when the user logs out.</p>
 */
public record RefreshToken(String value, Instant expiresAt, String userId) {
    
    public RefreshToken {
        Objects.requireNonNull(value, "Refresh token value cannot be null");
        Objects.requireNonNull(expiresAt, "Refresh token expiration cannot be null");
        Objects.requireNonNull(userId, "User ID cannot be null");
        
        if (value.isBlank()) {
            throw new IllegalArgumentException("Refresh token value cannot be blank");
        }
        if (userId.isBlank()) {
            throw new IllegalArgumentException("User ID cannot be blank");
        }
    }
    
    /**
     * Checks if the refresh token is expired.
     * 
     * @return true if the token has expired, false otherwise
     */
    public boolean isExpired() {
        return Instant.now().isAfter(expiresAt);
    }
}

