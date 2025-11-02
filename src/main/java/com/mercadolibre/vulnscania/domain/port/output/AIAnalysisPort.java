package com.mercadolibre.vulnscania.domain.port.output;

import com.mercadolibre.vulnscania.domain.model.application.Application;
import com.mercadolibre.vulnscania.domain.model.vulnerability.SeverityScore;
import com.mercadolibre.vulnscania.domain.model.vulnerability.Vulnerability;

/**
 * Output Port: AIAnalysisPort
 * Define el contrato para análisis de vulnerabilidades usando IA (LLMs).
 */
public interface AIAnalysisPort {
    
    /**
     * Analiza una vulnerabilidad en el contexto de una aplicación usando IA.
     */
    AIAnalysisResult analyze(Vulnerability vulnerability, Application application);
    
    /**
     * DTO para resultado de análisis de IA.
     */
    record AIAnalysisResult(
        SeverityScore suggestedScore,
        String justification,
        double confidenceLevel,
        String provider
    ) {
        public AIAnalysisResult {
            if (confidenceLevel < 0.0 || confidenceLevel > 1.0) {
                throw new IllegalArgumentException("Confidence level must be between 0.0 and 1.0");
            }
        }
    }
}

