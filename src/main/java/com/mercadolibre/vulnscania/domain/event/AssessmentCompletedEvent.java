package com.mercadolibre.vulnscania.domain.event;

import com.mercadolibre.vulnscania.domain.model.application.ApplicationId;
import com.mercadolibre.vulnscania.domain.model.assessment.AssessmentId;
import com.mercadolibre.vulnscania.domain.model.vulnerability.SeverityScore;
import com.mercadolibre.vulnscania.domain.model.vulnerability.VulnerabilityId;

import java.time.Instant;
import java.util.Objects;

/**
 * Evento: AssessmentCompletedEvent
 * Se dispara cuando se completa una evaluación de vulnerabilidad.
 * Útil para métricas, notificaciones y coordinación con otros sistemas.
 */
public record AssessmentCompletedEvent(
    AssessmentId assessmentId,
    VulnerabilityId vulnerabilityId,
    ApplicationId applicationId,
    SeverityScore finalScore,
    Double confidenceLevel,
    boolean requiresManualReview,
    Instant occurredAt
) implements DomainEvent {
    
    public AssessmentCompletedEvent {
        Objects.requireNonNull(assessmentId, "Assessment ID cannot be null");
        Objects.requireNonNull(vulnerabilityId, "Vulnerability ID cannot be null");
        Objects.requireNonNull(applicationId, "Application ID cannot be null");
        Objects.requireNonNull(finalScore, "Final score cannot be null");
        Objects.requireNonNull(occurredAt, "Occurred at cannot be null");
    }
}

