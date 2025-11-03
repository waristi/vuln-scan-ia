package com.mercadolibre.vulnscania.infrastructure.adapter.output.ai;

import com.mercadolibre.vulnscania.domain.model.ai.AIProvider;

/**
 * Constants for Spring bean names and infrastructure-specific AI provider mappings.
 * 
 * <p>Centralizes string literals used for Spring bean identification and provider aliases
 * to prevent typos and improve maintainability. Following DRY principle.</p>
 * 
 * <p><strong>Note</strong>: Domain provider names are in {@link AIProvider} class.
 * This class contains only infrastructure-specific constants (bean names, aliases).</p>
 */
public final class AIProviderConstants {
    
    /**
     * Default AI provider name (Gemini).
     * Delegates to domain constant for consistency.
     */
    public static final String DEFAULT_PROVIDER = AIProvider.DEFAULT;
    
    /**
     * Gemini provider name.
     * Delegates to domain constant for consistency.
     */
    public static final String PROVIDER_GEMINI = AIProvider.GEMINI;
    
    /**
     * OpenAI provider name.
     * Delegates to domain constant for consistency.
     */
    public static final String PROVIDER_OPENAI = AIProvider.OPENAI;
    
    /**
     * Claude provider name.
     * Delegates to domain constant for consistency.
     */
    public static final String PROVIDER_CLAUDE = AIProvider.CLAUDE;
    
    /**
     * Alternative names for OpenAI provider.
     * 
     * <p>These aliases can be used in place of "openai" when specifying a provider.
     * They are recognized by {@link AIProviderSelector#getBeanNameForProvider(String)}.</p>
     */
    public static final String[] OPENAI_ALIASES = {"gpt", "gpt-4"};
    
    /**
     * Alternative names for Claude provider.
     * 
     * <p>These aliases can be used in place of "claude" when specifying a provider.</p>
     */
    public static final String[] CLAUDE_ALIASES = {"anthropic"};
    
    /**
     * Alternative names for Gemini provider.
     * 
     * <p>These aliases can be used in place of "gemini" when specifying a provider.</p>
     */
    public static final String[] GEMINI_ALIASES = {"google"};
    
    /**
     * Spring bean name for Gemini adapter.
     * 
     * <p>This is the bean name used to retrieve the Gemini AI adapter from Spring's
     * ApplicationContext. Must match the bean name defined in configuration.</p>
     */
    public static final String BEAN_GEMINI_ADAPTER = "geminiAdapter";
    
    /**
     * Spring bean name for OpenAI adapter.
     * 
     * <p>This is the bean name used to retrieve the OpenAI AI adapter from Spring's
     * ApplicationContext. Must match the bean name defined in configuration.</p>
     */
    public static final String BEAN_OPENAI_ADAPTER = "openaiAdapter";
    
    /**
     * Spring bean name for Claude adapter.
     * 
     * <p>This is the bean name used to retrieve the Claude AI adapter from Spring's
     * ApplicationContext. Must match the bean name defined in configuration.</p>
     */
    public static final String BEAN_CLAUDE_ADAPTER = "claudeAdapter";
    
    /**
     * Spring bean name for fallback adapter.
     * 
     * <p>This is the bean name used to retrieve the fallback AI adapter, which is used
     * when no specific AI provider is configured or available.</p>
     */
    public static final String BEAN_FALLBACK_ADAPTER = "fallbackAIAnalysisAdapter";
    
    /**
     * Default provider bean names in priority order.
     * 
     * <p>This array defines the order in which providers are checked when a default
     * provider is needed. The first available provider in this list will be selected.</p>
     * 
     * <p><strong>Priority Order</strong>:</p>
     * <ol>
     *   <li>Gemini (highest priority - default)</li>
     *   <li>OpenAI</li>
     *   <li>Claude</li>
     *   <li>Fallback (lowest priority - always available if enabled)</li>
     * </ol>
     */
    public static final String[] DEFAULT_PROVIDER_BEANS = {
        BEAN_GEMINI_ADAPTER,
        BEAN_OPENAI_ADAPTER,
        BEAN_CLAUDE_ADAPTER,
        BEAN_FALLBACK_ADAPTER
    };
    
    /**
     * Private constructor to prevent instantiation.
     * This is a constants class and should never be instantiated.
     */
    private AIProviderConstants() {
        throw new UnsupportedOperationException("AIProviderConstants is a constants class and cannot be instantiated");
    }
}

