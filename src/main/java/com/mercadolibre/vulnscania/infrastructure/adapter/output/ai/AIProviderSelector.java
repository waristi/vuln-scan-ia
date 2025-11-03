package com.mercadolibre.vulnscania.infrastructure.adapter.output.ai;

import com.mercadolibre.vulnscania.domain.model.ai.AIProvider;
import com.mercadolibre.vulnscania.domain.port.output.AIAnalysisPort;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Component;

import static com.mercadolibre.vulnscania.infrastructure.adapter.output.ai.AIProviderConstants.*;

/**
 * Infrastructure service that dynamically selects AI provider adapters based on provider name.
 * 
 * <p>This component acts as a factory/resolver for AI provider implementations. It selects
 * the appropriate {@link AIAnalysisPort} adapter based on the provider name specified in
 * the request, falling back to a default provider if none is specified.</p>
 * 
 * <p><strong>Supported Providers</strong>:</p>
 * <ul>
 *   <li><strong>OpenAI</strong>: GPT-4 and other OpenAI models (aliases: "gpt", "gpt-4")</li>
 *   <li><strong>Claude</strong>: Anthropic Claude models (aliases: "anthropic")</li>
 *   <li><strong>Gemini</strong>: Google Gemini models (aliases: "google") - Default</li>
 * </ul>
 * 
 * <p><strong>Provider Resolution</strong>:</p>
 * <ol>
 *   <li>If provider name is provided: Returns the corresponding adapter bean</li>
 *   <li>If provider name is null/blank: Returns default provider (priority: Gemini > OpenAI > Claude > Fallback)</li>
 *   <li>If provider is not found: Throws {@link IllegalArgumentException}</li>
 * </ol>
 * 
 * <p><strong>Bean Naming Convention</strong>:</p>
 * <ul>
 *   <li>OpenAI: {@code "openaiAdapter"}</li>
 *   <li>Claude: {@code "claudeAdapter"}</li>
 *   <li>Gemini: {@code "geminiAdapter"}</li>
 *   <li>Fallback: {@code "fallbackAIAnalysisAdapter"}</li>
 * </ul>
 * 
 * @see AIAnalysisPort
 * @see AIProviderConstants
 * @see AIProvider
 */
@Component
public class AIProviderSelector {
    
    private final ApplicationContext applicationContext;
    
    /**
     * Constructs an AIProviderSelector with the Spring ApplicationContext.
     * 
     * <p>The ApplicationContext is used to dynamically retrieve AI provider adapter beans
     * by name at runtime, allowing for flexible provider selection without hard-coded
     * dependencies.</p>
     * 
     * @param applicationContext Spring ApplicationContext for bean lookup. Must not be null.
     */
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
        
        String beanName = getBeanNameForProvider(providerName);
        
        try {
            return applicationContext.getBean(beanName, AIAnalysisPort.class);
        } catch (Exception e) {
            throw new IllegalArgumentException(
                "AI provider '" + providerName + "' not found or not enabled. " +
                "Available providers: " + AIProvider.OPENAI + ", " + AIProvider.CLAUDE + ", " + AIProvider.GEMINI + ". " +
                "Make sure the provider is enabled in application.properties"
            );
        }
    }
    
    /**
     * Gets the default AI provider based on priority order.
     * 
     * <p>This method attempts to retrieve providers in the following priority order:</p>
     * <ol>
     *   <li>Gemini adapter</li>
     *   <li>OpenAI adapter</li>
     *   <li>Claude adapter</li>
     *   <li>Fallback adapter (always available if enabled)</li>
     * </ol>
     * 
     * <p>The first available provider is returned. If none are available, an
     * {@link IllegalStateException} is thrown.</p>
     * 
     * @return The first available AI provider adapter
     * @throws IllegalStateException if no AI provider is available in the application context
     */
    private AIAnalysisPort getDefaultProvider() {
        // Try providers in priority order
        for (String beanName : DEFAULT_PROVIDER_BEANS) {
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
     * Maps provider name (with aliases) to Spring bean name.
     * 
     * <p>This method normalizes provider names and maps them to their corresponding
     * Spring bean names. It supports multiple aliases for each provider to improve
     * user experience and flexibility.</p>
     * 
     * <p><strong>Supported Mappings</strong>:</p>
     * <ul>
     *   <li>"openai", "gpt", "gpt-4" → "openaiAdapter"</li>
     *   <li>"claude", "anthropic" → "claudeAdapter"</li>
     *   <li>"gemini", "google" → "geminiAdapter"</li>
     * </ul>
     * 
     * @param providerName The provider name (case-insensitive). Must not be null.
     * @return The corresponding Spring bean name
     * @throws IllegalArgumentException if the provider name is not recognized
     */
    private String getBeanNameForProvider(String providerName) {
        String normalizedName = providerName.toLowerCase();
        return switch (normalizedName) {
            case AIProvider.OPENAI, "gpt", "gpt-4" -> BEAN_OPENAI_ADAPTER;
            case AIProvider.CLAUDE, "anthropic" -> BEAN_CLAUDE_ADAPTER;
            case AIProvider.GEMINI, "google" -> BEAN_GEMINI_ADAPTER;
            default -> throw new IllegalArgumentException(
                "Unknown AI provider: " + providerName + ". " +
                "Supported values: " + AIProvider.OPENAI + ", " + AIProvider.CLAUDE + ", " + AIProvider.GEMINI
            );
        };
    }
    
    /**
     * Checks if a specific AI provider is available in the application context.
     * 
     * <p>This method is useful for validating provider availability before attempting
     * to use it, or for providing helpful error messages to users.</p>
     * 
     * @param providerName The provider name to check (case-insensitive). Can be null.
     * @return true if the provider is available and enabled, false otherwise
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

