package com.mercadolibre.vulnscania.infrastructure.adapter.output.ai;

import com.mercadolibre.vulnscania.domain.port.output.AIAnalysisPort;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Component;

/**
 * Service to select AI provider dynamically based on header or configuration.
 * Supports OpenAI, Claude, and Gemini.
 */
@Component
public class AIProviderSelector {
    
    private final ApplicationContext applicationContext;
    
    public AIProviderSelector(ApplicationContext applicationContext) {
        this.applicationContext = applicationContext;
    }
    
    /**
     * Gets AI provider based on provider name.
     * 
     * @param providerName Provider name: "openai", "claude", "gemini", or null for default
     * @return AIAnalysisPort implementation
     * @throws IllegalArgumentException if provider not found or not enabled
     */
    public AIAnalysisPort getProvider(String providerName) {
        if (providerName == null || providerName.isBlank()) {
            return getDefaultProvider();
        }
        
        String beanName = getBeanNameForProvider(providerName.toLowerCase());
        
        try {
            return applicationContext.getBean(beanName, AIAnalysisPort.class);
        } catch (Exception e) {
            throw new IllegalArgumentException(
                "AI provider '" + providerName + "' not found or not enabled. " +
                "Available providers: openai, claude, gemini. " +
                "Make sure the provider is enabled in application.properties"
            );
        }
    }
    
    /**
     * Gets default AI provider.
     * Priority order: gemini > openai > claude > fallback
     */
    private AIAnalysisPort getDefaultProvider() {
        // Try providers in priority order
        String[] beanNames = {"geminiAdapter", "openaiAdapter", "claudeAdapter", "fallbackAIAnalysisAdapter"};
        for (String beanName : beanNames) {
            try {
                return applicationContext.getBean(beanName, AIAnalysisPort.class);
            } catch (Exception e) {
                // Continue to next provider
            }
        }
        
        throw new IllegalStateException(
            "No AI provider available. Please enable at least one provider in application.properties"
        );
    }
    
    /**
     * Maps provider name to Spring bean name.
     */
    private String getBeanNameForProvider(String providerName) {
        return switch (providerName) {
            case "openai", "gpt", "gpt-4" -> "openaiAdapter";
            case "claude", "anthropic" -> "claudeAdapter";
            case "gemini", "google" -> "geminiAdapter";
            default -> throw new IllegalArgumentException(
                "Unknown AI provider: " + providerName + ". " +
                "Supported values: openai, claude, gemini"
            );
        };
    }
    
    /**
     * Checks if a specific provider is available.
     */
    public boolean isProviderAvailable(String providerName) {
        try {
            String beanName = getBeanNameForProvider(providerName.toLowerCase());
            return applicationContext.containsBean(beanName);
        } catch (Exception e) {
            return false;
        }
    }
}

