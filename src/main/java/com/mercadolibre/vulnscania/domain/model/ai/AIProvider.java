package com.mercadolibre.vulnscania.domain.model.ai;

/**
 * Domain constants for AI provider names.
 * 
 * <p>Represents the valid AI provider names used in the domain layer. These constants
 * represent business concepts (provider selection) rather than infrastructure details
 * (bean names, API endpoints, etc.).</p>
 * 
 * <p><strong>Purpose</strong>: Centralizes provider name literals to:</p>
 * <ul>
 *   <li>Prevent typos and string inconsistencies</li>
 *   <li>Provide a single source of truth for provider names</li>
 *   <li>Maintain domain layer independence from infrastructure</li>
 * </ul>
 * 
 * <p><strong>Note</strong>: Infrastructure-specific constants (bean names, aliases)
 * are defined in {@link com.mercadolibre.vulnscania.infrastructure.adapter.output.ai.AIProviderConstants}.</p>
 * 
 * @see com.mercadolibre.vulnscania.infrastructure.adapter.output.ai.AIProviderConstants
 */
public final class AIProvider {
    
    /**
     * Default AI provider name (Gemini).
     * 
     * <p>This is the provider used when no explicit provider is specified in the request.
     * Gemini is chosen as the default because it offers a good balance of cost, performance,
     * and availability.</p>
     */
    public static final String DEFAULT = "gemini";
    
    /**
     * Gemini provider name.
     * 
     * <p>Google's Gemini AI models, including Gemini 2.5 Flash.</p>
     */
    public static final String GEMINI = "gemini";
    
    /**
     * OpenAI provider name.
     * 
     * <p>OpenAI's GPT models, including GPT-4 Turbo.</p>
     */
    public static final String OPENAI = "openai";
    
    /**
     * Claude provider name.
     * 
     * <p>Anthropic's Claude models, including Claude 3.5 Sonnet.</p>
     */
    public static final String CLAUDE = "claude";
    
    /**
     * Private constructor to prevent instantiation.
     * 
     * <p>This is a utility class containing only constants and should never be instantiated.</p>
     * 
     * @throws UnsupportedOperationException always, as this class cannot be instantiated
     */
    private AIProvider() {
        throw new UnsupportedOperationException("AIProvider is a constants class and cannot be instantiated");
    }
}

