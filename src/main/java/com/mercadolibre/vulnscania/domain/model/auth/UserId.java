package com.mercadolibre.vulnscania.domain.model.auth;

import java.util.Objects;

/**
 * Value Object representing a User identifier.
 * 
 * <p>Immutable identifier for user entities, following DDD Value Object pattern.</p>
 */
public record UserId(String value) {
    
    public UserId {
        Objects.requireNonNull(value, "User ID value cannot be null");
        if (value.isBlank()) {
            throw new IllegalArgumentException("User ID value cannot be blank");
        }
    }
    
    /**
     * Creates a UserId from a string value.
     * 
     * @param value The user ID string value
     * @return UserId instance
     * @throws IllegalArgumentException if value is null or blank
     */
    public static UserId of(String value) {
        return new UserId(value);
    }
}

