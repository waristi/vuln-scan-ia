package com.mercadolibre.vulnscania.domain.event;

import com.mercadolibre.vulnscania.domain.model.vulnerability.SeverityScore;
import com.mercadolibre.vulnscania.domain.model.vulnerability.VulnerabilityId;

import java.time.Instant;
import java.util.Objects;

/**
 * Evento: ScoreAdjustedEvent
 * Se dispara cuando el score contextual de una vulnerabilidad es ajustado.
 * Útil para auditoría y tracking de cómo evoluciona la evaluación.
 */
public record ScoreAdjustedEvent(
    VulnerabilityId vulnerabilityId,
    SeverityScore oldScore,
    SeverityScore newScore,
    String justification,
    Instant occurredAt
) implements DomainEvent {
    
    public ScoreAdjustedEvent {
        Objects.requireNonNull(vulnerabilityId, "Vulnerability ID cannot be null");
        Objects.requireNonNull(oldScore, "Old score cannot be null");
        Objects.requireNonNull(newScore, "New score cannot be null");
        Objects.requireNonNull(occurredAt, "Occurred at cannot be null");
    }
    
    public double getAdjustment() {
        return newScore.value() - oldScore.value();
    }
}

