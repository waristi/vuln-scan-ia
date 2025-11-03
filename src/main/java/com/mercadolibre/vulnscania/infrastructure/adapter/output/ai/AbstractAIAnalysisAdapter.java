package com.mercadolibre.vulnscania.infrastructure.adapter.output.ai;

import com.mercadolibre.vulnscania.domain.constants.AIAssessmentConstants;
import com.mercadolibre.vulnscania.domain.model.application.Application;
import com.mercadolibre.vulnscania.domain.model.vulnerability.Vulnerability;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Abstract base class for AI analysis adapters.
 *
 * <p>Implements common functionality shared across all AI providers:</p>
 * <ul>
 *   <li><strong>Prompt Building</strong>: Standard format for vulnerability analysis requests</li>
 *   <li><strong>Response Validation</strong>: Score, justification, and confidence validation</li>
 *   <li><strong>Error Handling</strong>: Common error handling patterns</li>
 * </ul>
 *
 * <p><strong>Design Patterns Applied</strong>:</p>
 * <ul>
 *   <li><strong>Template Method</strong>: Subclasses implement provider-specific API calls</li>
 *   <li><strong>DRY</strong>: Common prompt logic shared across all providers</li>
 *   <li><strong>Single Responsibility</strong>: Prompt building separated from API communication</li>
 * </ul>
 *
 * @author Bernardo Zuluaga
 * @since 1.0.0
 * @see GeminiAnalysisAdapter
 * @see OpenAIAnalysisAdapter
 * @see ClaudeAnalysisAdapter
 */
public abstract class AbstractAIAnalysisAdapter {
    
    protected final Logger log = LoggerFactory.getLogger(getClass());
    
    /**
     * System prompt template for AI providers.
     *
     * <p>Defines the role and instructions for AI providers when analyzing
     * vulnerabilities in application context.</p>
     */
    protected static final String SYSTEM_PROMPT = """
        You are a security expert specializing in vulnerability assessment.
        Your task is to analyze a CVE vulnerability in the context of a specific application
        and provide a contextual risk score with justification.
        
        CRITICAL RULES:
        1. Your score must be between 0.0 and 10.0
        2. Provide a clear, technical justification (minimum 100 characters)
        3. Consider the application context: exposure, data sensitivity, environment
        4. Be conservative - when in doubt, err on the side of higher severity
        5. Respond in JSON format ONLY with fields: score, justification, confidence
        
        Response format:
        {
          "score": <number between 0.0 and 10.0>,
          "justification": "<detailed explanation>",
          "confidence": <number between 0.0 and 1.0>
        }
        """;

    /**
     * Builds the analysis prompt with vulnerability and application context.
     *
     * @param vulnerability the vulnerability to analyze
     * @param application the application context
     * @return the formatted analysis prompt
     * @throws NullPointerException if any parameter is null
     */
    protected String buildAnalysisPrompt(Vulnerability vulnerability, Application application) {
        return String.format("""
            Analyze this vulnerability:
            
            CVE ID: %s
            Base CVSS Score: %.1f
            Description: %s
            Type: %s
            
            Application Context:
            - Name: %s
            - Tech Stack: %s
            - Dependencies Count: %d
            - Internet Exposed: %s
            - Data Sensitivity: %s
            - Runtime Environments: %s
            - Production: %s
            - Known Mitigations: %s
            
            Provide a contextual risk score (0.0-10.0) considering:
            1. The base CVSS score
            2. Application exposure and environment
            3. Data sensitivity
            4. Existing mitigations
            5. Technology stack relevance
            
            Your confidence level should reflect certainty in your assessment.
            """,
            vulnerability.getCveId().value(),
            vulnerability.getBaseScore().value(),
            vulnerability.getDescription() != null ? vulnerability.getDescription() : "No description",
            vulnerability.getType().name(),
            application.getName(),
            application.getTechStack(),
            application.getDependencies().size(),
            application.isInternetExposed() ? "YES" : "NO",
            application.getDataSensitivity().name(),
            application.getRuntimeEnvironments(),
            application.isInProduction() ? "YES" : "NO",
            application.getKnownMitigations()
        );
    }
    
    /**
     * Validates AI response score is within acceptable CVSS range.
     *
     * <p>Valid range is [0.0, 10.0] according to CVSS specification.</p>
     *
     * @param score the score to validate
     * @return true if score is within valid CVSS range [0.0, 10.0]
     */
    protected boolean isValidScore(double score) {
        return score >= 0.0 && score <= 10.0;
    }

    /**
     * Validates justification is not empty and meets minimum length.
     *
     * <p>Minimum length of {@value AIAssessmentConstants#MIN_JUSTIFICATION_LENGTH} characters ensures
     * the justification is detailed enough to be useful for security analysts.</p>
     *
     * @param justification the justification text to validate
     * @return true if justification is valid and meets minimum length requirement
     */
    protected boolean isValidJustification(String justification) {
        return justification != null &&
               !justification.isBlank() &&
               justification.length() >= AIAssessmentConstants.MIN_JUSTIFICATION_LENGTH;
    }

    /**
     * Validates confidence level is within acceptable range.
     *
     * <p>Valid range is [0.0, 1.0] where 0.0 means no confidence
     * and 1.0 means complete confidence.</p>
     *
     * @param confidence the confidence level to validate
     * @return true if confidence is within valid range [0.0, 1.0]
     */
    protected boolean isValidConfidence(double confidence) {
        return confidence >= 0.0 && confidence <= 1.0;
    }
}


