package com.mercadolibre.vulnscania.infrastructure.adapter.output.ai;

import com.mercadolibre.vulnscania.domain.model.application.Application;
import com.mercadolibre.vulnscania.domain.model.vulnerability.Vulnerability;
import com.mercadolibre.vulnscania.domain.port.output.AIAnalysisPort;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Component;

/**
 * Fallback AI Analysis adapter that is used when no AI provider is configured.
 * This adapter returns the base CVSS score without AI enhancement.
 * 
 * <p>This bean has the lowest priority and will be used only when no other
 * AIAnalysisPort implementation is available, ensuring the application can start
 * even without AI provider configuration.</p>
 * 
 * @see AIAnalysisPort
 * @see OpenAIAnalysisAdapter
 * @see ClaudeAnalysisAdapter
 * @see GeminiAnalysisAdapter
 */
@Component
@ConditionalOnProperty(
    prefix = "ai", 
    name = "fallback.enabled", 
    havingValue = "true", 
    matchIfMissing = true
)
public class FallbackAIAnalysisAdapter extends AbstractAIAnalysisAdapter implements AIAnalysisPort {
    
    private static final String PROVIDER = "Fallback (No AI)";
    
    /**
     * Analyzes a vulnerability without AI enhancement.
     * Returns the base CVSS score from the vulnerability.
     * 
     * @param vulnerability The vulnerability to analyze
     * @param application The application context (not used in fallback)
     * @return Analysis result using base CVSS score with low confidence
     */
    @Override
    public AIAnalysisResult analyze(Vulnerability vulnerability, Application application) {
        log.info("Using fallback AI analysis for {} (no AI provider configured)", 
            vulnerability.getCveId().value());
        
        return new AIAnalysisResult(
            vulnerability.getBaseScore(),
            "No AI provider configured. Using base CVSS score without contextual analysis. " +
            "To enable AI-enhanced analysis, configure one of the supported providers: " +
            "OpenAI (ai.openai.enabled=true), Claude (ai.claude.enabled=true), or " +
            "Gemini (ai.gemini.enabled=true).",
            0.5, // Low confidence since no contextual analysis was performed
            PROVIDER
        );
    }
}

