package com.mercadolibre.vulnscania.domain.model.application;

import java.util.Objects;
import java.util.UUID;

/**
 * Value Object que representa el identificador único de una aplicación.
 */
public record ApplicationId(String value) {
    
    public ApplicationId {
        Objects.requireNonNull(value, "Application ID cannot be null");
        if (value.isBlank()) {
            throw new IllegalArgumentException("Application ID cannot be blank");
        }
    }
    
    public static ApplicationId generate() {
        return new ApplicationId(UUID.randomUUID().toString());
    }
    
    public static ApplicationId of(String value) {
        return new ApplicationId(value);
    }
}

