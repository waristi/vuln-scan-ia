package com.mercadolibre.vulnscania.domain.port.output;

import com.mercadolibre.vulnscania.domain.model.application.Application;
import com.mercadolibre.vulnscania.domain.model.vulnerability.SeverityScore;
import com.mercadolibre.vulnscania.domain.model.vulnerability.Vulnerability;

/**
 * Output Port for AI-based vulnerability analysis.
 * 
 * <p>Defines the contract for analyzing vulnerabilities using AI (LLMs) in the context
 * of an application. This port allows the domain layer to leverage AI capabilities
 * without depending on specific AI provider implementations.</p>
 * 
 * <p>Implementations of this port are provided by infrastructure adapters that interact
 * with various AI providers (OpenAI, Claude, Gemini). The port follows the hexagonal
 * architecture pattern, keeping the domain layer independent of external AI services.</p>
 * 
 * <p><strong>Usage</strong>:</p>
 * <pre>{@code
 * AIAnalysisPort aiPort = ...; // Injected by infrastructure
 * AIAnalysisResult result = aiPort.analyze(vulnerability, application);
 * }</pre>
 * 
 * @see com.mercadolibre.vulnscania.infrastructure.adapter.output.ai.OpenAIAnalysisAdapter
 * @see com.mercadolibre.vulnscania.infrastructure.adapter.output.ai.ClaudeAnalysisAdapter
 * @see com.mercadolibre.vulnscania.infrastructure.adapter.output.ai.GeminiAnalysisAdapter
 */
public interface AIAnalysisPort {
    
    /**
     * Analyzes a vulnerability in the context of an application using AI.
     * 
     * <p>This method performs contextual analysis by considering:</p>
     * <ul>
     *   <li>Vulnerability details (CVE, CVSS base score, description)</li>
     *   <li>Application context (technologies, dependencies, data sensitivity, environments)</li>
     *   <li>Business impact factors (exposure, criticality, exploitability)</li>
     * </ul>
     * 
     * <p>The AI provides a suggested severity score, justification, and confidence level
     * that can be used to enhance the deterministic baseline score.</p>
     * 
     * @param vulnerability The vulnerability to analyze. Must not be null.
     * @param application The application context where the vulnerability may be present. Must not be null.
     * @return Analysis result containing suggested score, justification, confidence level, and provider name
     * @throws IllegalArgumentException if vulnerability or application is null
     * @throws RuntimeException if AI service is unavailable or returns invalid response
     */
    AIAnalysisResult analyze(Vulnerability vulnerability, Application application);
    
    /**
     * Result of AI analysis containing suggested scoring and justification.
     * 
     * <p>This record encapsulates the output from an AI analysis, including:</p>
     * <ul>
     *   <li><strong>suggestedScore</strong>: AI's suggested CVSS severity score (0.0 - 10.0)</li>
     *   <li><strong>justification</strong>: Human-readable explanation of the score suggestion</li>
     *   <li><strong>confidenceLevel</strong>: AI's confidence in the analysis (0.0 - 1.0)</li>
     *   <li><strong>provider</strong>: Name of the AI provider that generated this analysis</li>
     * </ul>
     * 
     * <p><strong>Note</strong>: The suggested score should be validated and constrained
     * before use to prevent AI "hallucinations" and ensure it remains within reasonable
     * bounds relative to the baseline CVSS score.</p>
     * 
     * @param suggestedScore AI-suggested severity score (0.0 - 10.0)
     * @param justification Human-readable explanation of the score suggestion. Must be at least 50 characters.
     * @param confidenceLevel AI confidence level (0.0 - 1.0). Must be between 0.0 and 1.0.
     * @param provider Name of the AI provider (e.g., "OpenAI", "Claude", "Gemini")
     * @throws IllegalArgumentException if confidenceLevel is not between 0.0 and 1.0
     */
    record AIAnalysisResult(
        SeverityScore suggestedScore,
        String justification,
        double confidenceLevel,
        String provider
    ) {
        /**
         * Compact constructor that validates the confidence level.
         * 
         * @param suggestedScore the suggested severity score
         * @param justification the justification text
         * @param confidenceLevel the confidence level (must be 0.0 - 1.0)
         * @param provider the AI provider name
         * @throws IllegalArgumentException if confidenceLevel is not between 0.0 and 1.0
         */
        public AIAnalysisResult {
            if (confidenceLevel < 0.0 || confidenceLevel > 1.0) {
                throw new IllegalArgumentException(
                    "Confidence level must be between 0.0 and 1.0, got: " + confidenceLevel
                );
            }
        }
    }
}

