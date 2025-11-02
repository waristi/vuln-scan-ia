package com.mercadolibre.vulnscania.infrastructure.adapter.output.ai;

import com.mercadolibre.vulnscania.domain.model.application.Application;
import com.mercadolibre.vulnscania.domain.model.vulnerability.SeverityScore;
import com.mercadolibre.vulnscania.domain.model.vulnerability.Vulnerability;
import com.mercadolibre.vulnscania.domain.port.output.AIAnalysisPort;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Component;

/**
 * Google Gemini adapter implementing AIAnalysisPort.
 * 
 * TODO: Implement full Gemini API integration when API key is available.
 * This is a placeholder that returns fallback results.
 */
@Component
@ConditionalOnProperty(prefix = "ai.gemini", name = "enabled", havingValue = "true")
public class GeminiAnalysisAdapter extends AbstractAIAnalysisAdapter implements AIAnalysisPort {
    
    private static final String PROVIDER = "Google Gemini";
    
    @Override
    public AIAnalysisResult analyze(Vulnerability vulnerability, Application application) {
        log.warn("Gemini adapter not fully implemented - using fallback");
        
        return new AIAnalysisResult(
            vulnerability.getBaseScore(),
            "Gemini AI analysis not implemented - using base CVSS score",
            0.5,
            PROVIDER + " (not implemented)"
        );
    }
}

