package com.mercadolibre.vulnscania.infrastructure.adapter.output.ai;

/**
 * Infrastructure constants for AI provider configuration.
 *
 * <p>This class contains only infrastructure-specific constants used for configuring
 * AI provider adapters. Business rule constants are located in the domain layer
 * ({@link com.mercadolibre.vulnscania.domain.constants.AIAssessmentConstants}).</p>
 *
 * <p><strong>Configuration Categories</strong>:</p>
 * <ul>
 *   <li><strong>Gemini Configuration</strong>: Google Gemini model parameters</li>
 *   <li><strong>OpenAI Configuration</strong>: OpenAI model parameters</li>
 *   <li><strong>Claude Configuration</strong>: Anthropic Claude model parameters</li>
 *   <li><strong>Response Parsing</strong>: Constants for parsing AI responses</li>
 * </ul>
 *
 * @author Bernardo Zuluaga
 * @since 1.0.0
 * @see AbstractAIAnalysisAdapter
 * @see com.mercadolibre.vulnscania.domain.constants.AIAssessmentConstants
 */
public final class AIAnalysisConstants {
    
    /**
     * Private constructor to prevent instantiation.
     *
     * <p>This is a constants class and should never be instantiated.</p>
     */
    private AIAnalysisConstants() {
        throw new UnsupportedOperationException("AIAnalysisConstants is a constants class and cannot be instantiated");
    }

    /**
     * Gemini model temperature parameter.
     *
     * <p>Range: 0.0 to 1.0</p>
     * <p>Lower temperature (0.3) produces more deterministic, focused responses.
     * This is appropriate for vulnerability assessment where consistency is critical.</p>
     *
     * <p>Value: 0.3</p>
     *
     * @see <a href="https://ai.google.dev/gemini-api/docs/models/generative-models#generation-config">Gemini API Documentation</a>
     */
    public static final double GEMINI_TEMPERATURE = 0.3;

    /**
     * Maximum output tokens for Gemini 2.5 Flash model.
     *
     * <p>Gemini 2.5 models use internal "thoughts" tokens (~500-1000) before generating
     * output. Setting this to 2048 ensures enough room for both thinking and response.</p>
     *
     * <p><strong>Note</strong>: Lower values (e.g. 1000) may result in truncated responses
     * with finishReason="MAX_TOKENS".</p>
     *
     * <p>Value: 2048 tokens</p>
     *
     * @see <a href="https://ai.google.dev/gemini-api/docs/models/gemini-2.5-flash">Gemini 2.5 Flash Documentation</a>
     */
    public static final int GEMINI_MAX_OUTPUT_TOKENS = 2048;

    /**
     * Gemini top_p (nucleus sampling) parameter.
     *
     * <p>Range: 0.0 to 1.0</p>
     * <p>Value of 0.8 provides a balance between creativity and focus, sampling from the
     * top 80% probability mass.</p>
     *
     * <p>Value: 0.8</p>
     */
    public static final double GEMINI_TOP_P = 0.8;

    /**
     * Gemini top_k parameter.
     *
     * <p>Limits sampling to top 40 tokens, providing consistent and focused responses.</p>
     *
     * <p>Value: 40</p>
     */
    public static final int GEMINI_TOP_K = 40;

    /**
     * API timeout for Gemini requests in seconds.
     *
     * <p>Value: 30 seconds</p>
     */
    public static final long GEMINI_TIMEOUT_SECONDS = 30;

    /**
     * Length of JSON markdown marker string.
     *
     * <p>Used for parsing JSON responses wrapped in markdown code blocks.</p>
     *
     * <p>Value: 7 characters ("```json")</p>
     */
    public static final int JSON_MARKER_LENGTH = 7;

    /**
     * Length of code block marker string.
     *
     * <p>Used for parsing code blocks in AI responses.</p>
     *
     * <p>Value: 3 characters ("```")</p>
     */
    public static final int CODE_BLOCK_MARKER_LENGTH = 3;

    /**
     * OpenAI model temperature parameter.
     *
     * <p>Range: 0.0 to 1.0</p>
     * <p>Lower temperature for deterministic security assessments, consistent with
     * other AI providers.</p>
     *
     * <p>Value: 0.3</p>
     */
    public static final double OPENAI_TEMPERATURE = 0.3;

    /**
     * Maximum tokens for OpenAI models.
     *
     * <p>2000 tokens provides enough space for detailed analysis and justification.</p>
     *
     * <p>Value: 2000 tokens</p>
     */
    public static final int OPENAI_MAX_TOKENS = 2000;

    /**
     * API timeout for OpenAI requests in seconds.
     *
     * <p>Value: 30 seconds</p>
     */
    public static final long OPENAI_TIMEOUT_SECONDS = 30;

    /**
     * Claude model temperature parameter.
     *
     * <p>Range: 0.0 to 1.0</p>
     * <p>Consistent with other providers for deterministic security assessments.</p>
     *
     * <p>Value: 0.3</p>
     */
    public static final double CLAUDE_TEMPERATURE = 0.3;

    /**
     * Maximum tokens for Claude models.
     *
     * <p>2000 tokens provides enough space for detailed analysis and justification.</p>
     *
     * <p>Value: 2000 tokens</p>
     */
    public static final int CLAUDE_MAX_TOKENS = 2000;

    /**
     * API timeout for Claude requests in seconds.
     *
     * <p>Value: 30 seconds</p>
     */
    public static final long CLAUDE_TIMEOUT_SECONDS = 30;
}

