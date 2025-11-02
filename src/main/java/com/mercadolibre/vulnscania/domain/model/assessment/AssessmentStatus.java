package com.mercadolibre.vulnscania.domain.model.assessment;

/**
 * Estado de una evaluación de vulnerabilidad.
 */
public enum AssessmentStatus {
    
    /**
     * Evaluación iniciada, en progreso.
     */
    IN_PROGRESS,
    
    /**
     * Evaluación completada exitosamente.
     */
    COMPLETED,
    
    /**
     * Evaluación falló por error técnico.
     */
    FAILED,
    
    /**
     * Evaluación requiere revisión manual adicional.
     */
    REQUIRES_REVIEW;
    
    /**
     * Determina si la evaluación está finalizada.
     */
    public boolean isFinished() {
        return this == COMPLETED || this == FAILED;
    }
    
    /**
     * Determina si se puede modificar.
     */
    public boolean canBeModified() {
        return this == IN_PROGRESS;
    }
}

