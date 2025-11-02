package com.mercadolibre.vulnscania.infrastructure.adapter.output.ai;

import com.mercadolibre.vulnscania.domain.model.application.Application;
import com.mercadolibre.vulnscania.domain.model.vulnerability.Vulnerability;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Abstract base class for AI analysis adapters.
 * Implements common functionality for prompt building and error handling.
 * 
 * Clean code principles:
 * - DRY: Common prompt logic shared across providers
 * - Template Method pattern: subclasses implement providerSpecificAnalysis()
 * - Single Responsibility: prompt building separated from API communication
 */
public abstract class AbstractAIAnalysisAdapter {
    
    protected final Logger log = LoggerFactory.getLogger(getClass());
    
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
     * Validates AI response score is within acceptable range.
     */
    protected boolean isValidScore(double score) {
        return score >= 0.0 && score <= 10.0;
    }
    
    /**
     * Validates justification is not empty and meets minimum length.
     */
    protected boolean isValidJustification(String justification) {
        return justification != null && 
               !justification.isBlank() && 
               justification.length() >= 50;
    }
    
    /**
     * Validates confidence is within acceptable range.
     */
    protected boolean isValidConfidence(double confidence) {
        return confidence >= 0.0 && confidence <= 1.0;
    }
}

