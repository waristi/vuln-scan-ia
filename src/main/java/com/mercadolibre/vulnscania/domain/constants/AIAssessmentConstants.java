package com.mercadolibre.vulnscania.domain.constants;

/**
 * Domain constants for AI assessment validation business rules.
 *
 * <p>This class contains all business rule constants used in AI vulnerability assessment
 * validation. These constants define thresholds and limits that are part of the core
 * domain logic for ensuring AI analysis reliability and preventing hallucinations.</p>
 *
 * <p><strong>Constants Categories</strong>:</p>
 * <ul>
 *   <li><strong>Validation Thresholds</strong>: Minimum acceptable values for AI responses</li>
 *   <li><strong>Score Constraints</strong>: Limits for AI score adjustments</li>
 * </ul>
 *
 * <p>These constants belong to the domain layer because they represent business rules
 * that are independent of any specific AI provider implementation.</p>
 *
 * @see com.mercadolibre.vulnscania.domain.service.AIAssessmentValidationService
 * @author Bernardo Zuluaga
 * @since 1.0.0
 */
public final class AIAssessmentConstants {

    /**
     * Minimum acceptable length for AI justification in characters.
     *
     * <p>This ensures that justifications are detailed enough to be useful
     * for security analysts when reviewing vulnerability assessments.</p>
     *
     * <p>Value: 100 characters</p>
     */
    public static final int MIN_JUSTIFICATION_LENGTH = 100;

    /**
     * Fallback minimum justification length for error cases in characters.
     *
     * <p>Used when validating shorter justifications in fallback scenarios
     * or error recovery situations.</p>
     *
     * <p>Value: 50 characters</p>
     */
    public static final int FALLBACK_MIN_JUSTIFICATION_LENGTH = 50;

    /**
     * Minimum acceptable confidence level for AI analysis.
     *
     * <p>Range: 0.0 to 1.0</p>
     * <p>Below this threshold, the AI analysis is considered unreliable
     * and will be rejected in favor of deterministic scoring.</p>
     *
     * <p>Value: 0.6</p>
     */
    public static final double MIN_CONFIDENCE_ACCEPTABLE = 0.6;

    /**
     * Minimum confidence level to avoid requiring human review.
     *
     * <p>Range: 0.0 to 1.0</p>
     * <p>If confidence is below this threshold, the assessment will be
     * flagged for manual review by a security analyst.</p>
     *
     * <p>Value: 0.7</p>
     */
    public static final double MIN_CONFIDENCE_FOR_NO_REVIEW = 0.7;

    /**
     * Default confidence level for fallback scenarios.
     *
     * <p>Range: 0.0 to 1.0</p>
     * <p>Used when AI provider fails or returns invalid confidence values.</p>
     *
     * <p>Value: 0.5</p>
     */
    public static final double DEFAULT_FALLBACK_CONFIDENCE = 0.5;

    /**
     * High confidence level for optimistic fallback scenarios.
     *
     * <p>Range: 0.0 to 1.0</p>
     * <p>Used when we have strong deterministic indicators to support
     * the assessment.</p>
     *
     * <p>Value: 0.7</p>
     */
    public static final double DEFAULT_HIGH_CONFIDENCE = 0.7;

    /**
     * Maximum allowed adjustment from baseline score by AI in CVSS points.
     *
     * <p>AI can adjust the score by at most ±1.5 points from the deterministic
     * baseline to prevent hallucinations and maintain consistency with the
     * deterministic model.</p>
     *
     * <p><strong>Business Rule</strong>: We trust AI to refine scores within a
     * reasonable range, but not to completely override deterministic scoring.</p>
     *
     * <p>Value: 1.5 CVSS points</p>
     */
    public static final double MAX_AI_ADJUSTMENT = 1.5;

    /**
     * Maximum adjustment before rejecting AI analysis in CVSS points.
     *
     * <p>If AI suggests an adjustment larger than this value, the entire
     * analysis is rejected as potentially unreliable or hallucinated.</p>
     *
     * <p>Value: 3.0 CVSS points</p>
     */
    public static final double MAX_ADJUSTMENT_BEFORE_REJECTION = 3.0;

    /**
     * Maximum multiplier for score increase.
     *
     * <p>Prevents panic scoring where AI dramatically overestimates severity.
     * AI cannot suggest more than 2x the baseline score.</p>
     *
     * <p>Example: If baseline is 5.0, AI cannot suggest more than 10.0
     * (which will be clamped to CVSS max of 10.0)</p>
     *
     * <p>Value: 2.0</p>
     */
    public static final double MAX_SCORE_MULTIPLIER = 2.0;

    /**
     * Maximum weight given to AI score when blending with baseline.
     *
     * <p>Range: 0.0 to 1.0</p>
     * <p>Even with perfect confidence, AI gets at most 70% weight, ensuring the
     * deterministic baseline always has significant influence (minimum 30%).</p>
     *
     * <p><strong>Formula</strong>: finalScore = (baseline * 0.3) + (aiScore * 0.7)</p>
     *
     * <p>Value: 0.7</p>
     */
    public static final double MAX_AI_WEIGHT = 0.7;

    /**
     * Minimum discrepancy between AI and baseline to flag for review in CVSS points.
     *
     * <p>Large discrepancies indicate uncertainty or potential issues with either
     * the deterministic model or AI analysis, requiring human verification.</p>
     *
     * <p>Value: 2.0 CVSS points</p>
     */
    public static final double MIN_DISCREPANCY_FOR_REVIEW = 2.0;

    /**
     * Private constructor to prevent instantiation.
     *
     * <p>This is a constants class and should never be instantiated.</p>
     */
    private AIAssessmentConstants() {
        throw new UnsupportedOperationException("AIAssessmentConstants is a constants class and cannot be instantiated");
    }
}

