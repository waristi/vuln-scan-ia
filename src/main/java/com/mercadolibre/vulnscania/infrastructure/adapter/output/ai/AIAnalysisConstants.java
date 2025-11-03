package com.mercadolibre.vulnscania.infrastructure.adapter.output.ai;

/**
 * Constants for AI analysis configuration.
 * 
 * <p>Centralizes all magic numbers and configuration values used across
 * AI adapters and validation services. This follows the DRY principle and
 * makes configuration values easier to maintain and understand.</p>
 * 
 * <p>Configuration categories:</p>
 * <ul>
 *   <li><strong>Validation Thresholds</strong>: Minimum acceptable values for AI responses</li>
 *   <li><strong>Score Constraints</strong>: Limits for AI score adjustments</li>
 *   <li><strong>Gemini Configuration</strong>: Google Gemini model parameters</li>
 *   <li><strong>OpenAI Configuration</strong>: OpenAI model parameters</li>
 *   <li><strong>Claude Configuration</strong>: Anthropic Claude model parameters</li>
 * </ul>
 * 
 * @see AbstractAIAnalysisAdapter
 * @see com.mercadolibre.vulnscania.domain.service.AIAssessmentValidationService
 */
public final class AIAnalysisConstants {
    
    // ========== Validation Thresholds ==========
    
    /**
     * Minimum acceptable length for AI justification (in characters).
     * 
     * <p>100 characters ensures the justification is detailed enough
     * to be useful for security analysts.</p>
     */
    public static final int MIN_JUSTIFICATION_LENGTH = 100;
    
    /**
     * Fallback minimum justification length for error cases (in characters).
     * 
     * <p>Used when we need to validate a shorter justification,
     * for example in fallback scenarios.</p>
     */
    public static final int FALLBACK_MIN_JUSTIFICATION_LENGTH = 50;
    
    /**
     * Minimum acceptable confidence level for AI analysis (0.0 - 1.0).
     * 
     * <p>Below this threshold, the AI analysis is considered unreliable
     * and will be rejected in favor of deterministic scoring.</p>
     */
    public static final double MIN_CONFIDENCE_ACCEPTABLE = 0.6;
    
    /**
     * Minimum confidence level to avoid requiring human review (0.0 - 1.0).
     * 
     * <p>If confidence is below this threshold, the assessment will be
     * flagged for manual review by a security analyst.</p>
     */
    public static final double MIN_CONFIDENCE_FOR_NO_REVIEW = 0.7;
    
    /**
     * Default confidence level for fallback scenarios (0.0 - 1.0).
     * 
     * <p>Used when AI provider fails or returns invalid confidence.</p>
     */
    public static final double DEFAULT_FALLBACK_CONFIDENCE = 0.5;
    
    /**
     * High confidence level for optimistic fallback (0.0 - 1.0).
     * 
     * <p>Used when we have strong deterministic indicators.</p>
     */
    public static final double DEFAULT_HIGH_CONFIDENCE = 0.7;
    
    // ========== Score Constraints ==========
    
    /**
     * Maximum allowed adjustment from baseline score by AI (in CVSS points).
     * 
     * <p>AI can adjust the score by at most ±1.5 points from the deterministic
     * baseline to prevent "hallucinations" and maintain consistency.</p>
     * 
     * <p><strong>Business Rule</strong>: We trust AI to refine scores within a
     * reasonable range, but not to completely override deterministic scoring.</p>
     */
    public static final double MAX_AI_ADJUSTMENT = 1.5;
    
    /**
     * Maximum adjustment before rejecting AI analysis (in CVSS points).
     * 
     * <p>If AI suggests an adjustment larger than this value, the entire
     * analysis is rejected as potentially unreliable.</p>
     */
    public static final double MAX_ADJUSTMENT_BEFORE_REJECTION = 3.0;
    
    /**
     * Maximum multiplier for score increase (2x baseline).
     * 
     * <p>Prevents "panic scoring" where AI dramatically overestimates severity.</p>
     * 
     * <p>Example: If baseline is 5.0, AI cannot suggest more than 10.0 (but will
     * be clamped to CVSS max of 10.0)</p>
     */
    public static final double MAX_SCORE_MULTIPLIER = 2.0;
    
    /**
     * Maximum weight given to AI score when blending with baseline (0.0 - 1.0).
     * 
     * <p>Even with perfect confidence, AI gets at most 70% weight, ensuring the
     * deterministic baseline always has significant influence (minimum 30%).</p>
     * 
     * <p><strong>Formula</strong>: finalScore = (baseline * 0.3) + (aiScore * 0.7)</p>
     */
    public static final double MAX_AI_WEIGHT = 0.7;
    
    /**
     * Minimum discrepancy between AI and baseline to flag for review (in CVSS points).
     * 
     * <p>Large discrepancies indicate uncertainty or potential issues with either
     * the deterministic model or AI analysis.</p>
     */
    public static final double MIN_DISCREPANCY_FOR_REVIEW = 2.0;
    
    // ========== Gemini Configuration ==========
    
    /**
     * Gemini model temperature (0.0 - 1.0).
     * 
     * <p>Lower temperature (0.3) produces more deterministic, focused responses.
     * This is appropriate for vulnerability assessment where consistency is critical.</p>
     * 
     * <p>Reference: @https://ai.google.dev/gemini-api/docs/models/generative-models#generation-config</p>
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
     * <p>Reference: @https://ai.google.dev/gemini-api/docs/models/gemini-2.5-flash</p>
     */
    public static final int GEMINI_MAX_OUTPUT_TOKENS = 2048;
    
    /**
     * Gemini top_p (nucleus sampling) parameter (0.0 - 1.0).
     * 
     * <p>0.8 provides a balance between creativity and focus, sampling from the
     * top 80% probability mass.</p>
     */
    public static final double GEMINI_TOP_P = 0.8;
    
    /**
     * Gemini top_k parameter.
     * 
     * <p>Limits sampling to top 40 tokens, providing consistent and focused responses.</p>
     */
    public static final int GEMINI_TOP_K = 40;
    
    /**
     * API timeout for Gemini requests (in seconds).
     */
    public static final long GEMINI_TIMEOUT_SECONDS = 30;
    
    /**
     * Length of JSON markdown marker "```json".
     */
    public static final int JSON_MARKER_LENGTH = 7;
    
    /**
     * Length of code block marker "```".
     */
    public static final int CODE_BLOCK_MARKER_LENGTH = 3;
    
    // ========== OpenAI Configuration ==========
    
    /**
     * OpenAI model temperature (0.0 - 1.0).
     * 
     * <p>Same rationale as Gemini: lower temperature for deterministic security assessments.</p>
     */
    public static final double OPENAI_TEMPERATURE = 0.3;
    
    /**
     * Maximum tokens for OpenAI models.
     * 
     * <p>2000 tokens provides enough space for detailed analysis and justification.</p>
     */
    public static final int OPENAI_MAX_TOKENS = 2000;
    
    /**
     * API timeout for OpenAI requests (in seconds).
     */
    public static final long OPENAI_TIMEOUT_SECONDS = 30;
    
    // ========== Claude Configuration ==========
    
    /**
     * Claude model temperature (0.0 - 1.0).
     * 
     * <p>Consistent with other providers for deterministic security assessments.</p>
     */
    public static final double CLAUDE_TEMPERATURE = 0.3;
    
    /**
     * Maximum tokens for Claude models.
     */
    public static final int CLAUDE_MAX_TOKENS = 2000;
    
    /**
     * API timeout for Claude requests (in seconds).
     */
    public static final long CLAUDE_TIMEOUT_SECONDS = 30;
    
    // ========== Private Constructor ==========
    
    /**
     * Private constructor to prevent instantiation.
     * This is a constants class and should never be instantiated.
     */
    private AIAnalysisConstants() {
        throw new UnsupportedOperationException("AIAnalysisConstants is a constants class and cannot be instantiated");
    }
}

