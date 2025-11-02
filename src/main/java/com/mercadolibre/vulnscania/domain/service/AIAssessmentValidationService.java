package com.mercadolibre.vulnscania.domain.service;

import com.mercadolibre.vulnscania.domain.model.vulnerability.SeverityScore;

/**
 * Domain Service: AIAssessmentValidationService
 * Valida y controla los resultados del análisis de IA para prevenir "alucinaciones" y garantizar coherencia.
 * 
 * Reglas de negocio críticas para uso seguro de IA en evaluación de vulnerabilidades.
 */
public class AIAssessmentValidationService {
    
    private static final double MAX_ALLOWED_ADJUSTMENT = 1.5;
    private static final double MIN_CONFIDENCE_THRESHOLD = 0.6;
    
    /**
     * Valida y ajusta un score sugerido por IA contra el baseline determinista.
     * 
     * Reglas de seguridad:
     * - IA no puede ajustar más de ±1.5 puntos del baseline
     * - IA no puede bajar score si baseline es crítico (>= 9.0)
     * - IA no puede subir más de 2x el baseline
     * 
     * @param aiSuggestedScore Score sugerido por IA
     * @param baselineScore Score calculado determinísticamente
     * @return Score validado y ajustado si es necesario
     */
    public SeverityScore validateAndConstrainScore(SeverityScore aiSuggestedScore, 
                                                    SeverityScore baselineScore) {
        double ai = aiSuggestedScore.value();
        double baseline = baselineScore.value();
        
        // Regla 1: No permitir ajustes mayores a ±1.5
        double maxAllowed = baseline + MAX_ALLOWED_ADJUSTMENT;
        double minAllowed = baseline - MAX_ALLOWED_ADJUSTMENT;
        
        if (ai > maxAllowed) {
            ai = maxAllowed;
        } else if (ai < minAllowed) {
            ai = minAllowed;
        }
        
        // Regla 2: No bajar score si baseline es crítico
        if (baselineScore.isCritical() && ai < baseline) {
            ai = baseline;
        }
        
        // Regla 3: No subir más de 2x el baseline (prevenir "panic scoring")
        double maxDouble = baseline * 2.0;
        if (ai > maxDouble) {
            ai = maxDouble;
        }
        
        // Clamp al rango válido
        ai = Math.min(SeverityScore.MAX_SCORE, Math.max(SeverityScore.MIN_SCORE, ai));
        
        return new SeverityScore(ai);
    }
    
    /**
     * Determina si el análisis de IA es confiable basado en el nivel de confianza reportado.
     * 
     * @param confidenceLevel Nivel de confianza (0.0 - 1.0)
     * @return true si es confiable (>= 0.6)
     */
    public boolean isConfidenceAcceptable(double confidenceLevel) {
        return confidenceLevel >= MIN_CONFIDENCE_THRESHOLD;
    }
    
    /**
     * Determina si el análisis de IA debe ser rechazado y usar solo baseline.
     * 
     * Reglas:
     * - Confianza < 0.6
     * - Ajuste sugerido muy grande (> 3.0 puntos)
     * - Justificación vacía o muy corta (< 50 caracteres)
     * 
     * @param aiScore Score de IA
     * @param baselineScore Score baseline
     * @param confidenceLevel Confianza
     * @param justification Justificación del IA
     * @return true si debe rechazarse
     */
    public boolean shouldRejectAIAnalysis(SeverityScore aiScore,
                                         SeverityScore baselineScore,
                                         double confidenceLevel,
                                         String justification) {
        // Confianza muy baja
        if (confidenceLevel < MIN_CONFIDENCE_THRESHOLD) {
            return true;
        }
        
        // Ajuste demasiado grande (potencial alucinación)
        double adjustment = Math.abs(aiScore.value() - baselineScore.value());
        if (adjustment > 3.0) {
            return true;
        }
        
        // Justificación insuficiente
        if (justification == null || justification.length() < 50) {
            return true;
        }
        
        return false;
    }
    
    /**
     * Combina el score de IA con el baseline usando un peso basado en confianza.
     * 
     * Regla: Score final = (baseline * (1 - weight)) + (aiScore * weight)
     * Donde weight está determinado por el nivel de confianza.
     * 
     * @param aiScore Score de IA
     * @param baselineScore Score baseline
     * @param confidenceLevel Nivel de confianza (0.0 - 1.0)
     * @return Score combinado ponderado
     */
    public SeverityScore blendScores(SeverityScore aiScore,
                                     SeverityScore baselineScore,
                                     double confidenceLevel) {
        // Confianza baja: más peso al baseline
        // Confianza alta: más peso al IA
        double aiWeight = Math.min(0.7, confidenceLevel); // Máximo 70% de peso a IA
        double baselineWeight = 1.0 - aiWeight;
        
        double blended = (baselineScore.value() * baselineWeight) + 
                        (aiScore.value() * aiWeight);
        
        return new SeverityScore(blended);
    }
    
    /**
     * Determina si el análisis requiere revisión humana.
     * 
     * Requiere revisión si:
     * - Confianza < 0.7
     * - Score final es crítico (>= 9.0)
     * - Discrepancia grande entre IA y baseline (> 2.0)
     * 
     * @param finalScore Score final
     * @param aiScore Score de IA
     * @param baselineScore Score baseline
     * @param confidenceLevel Confianza
     * @return true si requiere revisión
     */
    public boolean requiresHumanReview(SeverityScore finalScore,
                                      SeverityScore aiScore,
                                      SeverityScore baselineScore,
                                      double confidenceLevel) {
        // Confianza moderada-baja
        if (confidenceLevel < 0.7) {
            return true;
        }
        
        // Score crítico siempre requiere confirmación
        if (finalScore.isCritical()) {
            return true;
        }
        
        // Gran discrepancia entre IA y baseline
        double discrepancy = Math.abs(aiScore.value() - baselineScore.value());
        if (discrepancy > 2.0) {
            return true;
        }
        
        return false;
    }
}

