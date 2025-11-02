package com.mercadolibre.vulnscania.domain.model.application;

/**
 * Nivel de sensibilidad de los datos manejados por una aplicación.
 * Influye en el cálculo de riesgo contextual.
 */
public enum DataSensitivity {
    
    /**
     * Datos públicos, sin información sensible.
     */
    PUBLIC(1.0),
    
    /**
     * Datos internos, no críticos.
     */
    INTERNAL(1.1),
    
    /**
     * Datos confidenciales (PII no sensible, información de negocio).
     */
    CONFIDENTIAL(1.3),
    
    /**
     * Datos sensibles (PII sensible, datos financieros).
     */
    SENSITIVE(1.5),
    
    /**
     * Datos altamente regulados (salud, financiero crítico).
     */
    HIGHLY_REGULATED(1.8);
    
    private final double riskMultiplier;
    
    DataSensitivity(double riskMultiplier) {
        this.riskMultiplier = riskMultiplier;
    }
    
    /**
     * Retorna el multiplicador de riesgo para ajustar scores.
     */
    public double getRiskMultiplier() {
        return riskMultiplier;
    }
    
    /**
     * Determina si requiere protección especial.
     */
    public boolean requiresSpecialProtection() {
        return this == SENSITIVE || this == HIGHLY_REGULATED;
    }
}

