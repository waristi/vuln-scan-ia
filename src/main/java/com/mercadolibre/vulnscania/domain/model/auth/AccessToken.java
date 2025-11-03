package com.mercadolibre.vulnscania.domain.model.auth;

import java.time.Instant;
import java.util.Objects;

/**
 * Value Object representing an access token.
 * 
 * <p>Access tokens are short-lived (typically 1 hour) and used for API authentication.
 * They contain user identity and are validated on each request.</p>
 */
public record AccessToken(String value, Instant expiresAt, String userId) {
    
    public AccessToken {
        Objects.requireNonNull(value, "Access token value cannot be null");
        Objects.requireNonNull(expiresAt, "Access token expiration cannot be null");
        Objects.requireNonNull(userId, "User ID cannot be null");
        
        if (value.isBlank()) {
            throw new IllegalArgumentException("Access token value cannot be blank");
        }
        if (userId.isBlank()) {
            throw new IllegalArgumentException("User ID cannot be blank");
        }
    }
    
    /**
     * Checks if the access token is expired.
     * 
     * @return true if the token has expired, false otherwise
     */
    public boolean isExpired() {
        return Instant.now().isAfter(expiresAt);
    }
}

