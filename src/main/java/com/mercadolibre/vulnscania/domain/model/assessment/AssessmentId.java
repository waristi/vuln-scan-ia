package com.mercadolibre.vulnscania.domain.model.assessment;

import java.util.Objects;
import java.util.UUID;

/**
 * Value Object que representa el identificador único de una evaluación.
 */
public record AssessmentId(String value) {
    
    public AssessmentId {
        Objects.requireNonNull(value, "Assessment ID cannot be null");
        if (value.isBlank()) {
            throw new IllegalArgumentException("Assessment ID cannot be blank");
        }
    }
    
    public static AssessmentId generate() {
        return new AssessmentId(UUID.randomUUID().toString());
    }
    
    public static AssessmentId of(String value) {
        return new AssessmentId(value);
    }
}

