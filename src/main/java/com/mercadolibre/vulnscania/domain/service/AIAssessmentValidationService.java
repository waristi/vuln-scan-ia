package com.mercadolibre.vulnscania.domain.service;

import com.mercadolibre.vulnscania.domain.constants.AIAssessmentConstants;
import com.mercadolibre.vulnscania.domain.model.vulnerability.SeverityScore;

import static com.mercadolibre.vulnscania.domain.constants.AIAssessmentConstants.*;

/**
 * Domain Service for validating and controlling AI analysis results.
 *
 * <p>This service implements critical business rules for safe use of AI in vulnerability
 * assessment. It prevents AI hallucinations and ensures consistency with deterministic
 * scoring models.</p>
 *
 * <p><strong>Core Responsibilities</strong>:</p>
 * <ul>
 *   <li>Validate AI-suggested scores against deterministic baselines</li>
 *   <li>Prevent AI from making unreasonable adjustments</li>
 *   <li>Blend AI and baseline scores based on confidence levels</li>
 *   <li>Determine when human review is required</li>
 * </ul>
 *
 * <p><strong>Key Business Rules</strong>:</p>
 * <ul>
 *   <li>AI cannot adjust more than ±{@value AIAssessmentConstants#MAX_AI_ADJUSTMENT} points from baseline</li>
 *   <li>AI cannot reduce critical scores (>= 9.0)</li>
 *   <li>AI gets maximum {@value AIAssessmentConstants#MAX_AI_WEIGHT} weight when blending scores</li>
 *   <li>Low confidence (<{@value AIAssessmentConstants#MIN_CONFIDENCE_ACCEPTABLE}) triggers rejection</li>
 *   <li>Large discrepancies (>{@value AIAssessmentConstants#MIN_DISCREPANCY_FOR_REVIEW}) require human review</li>
 * </ul>
 *
 * <p>This is a domain service because it contains business logic that doesn't naturally
 * belong to any single entity but coordinates between Vulnerability aggregate and AI analysis.</p>
 *
 * @author Bernardo Zuluaga
 * @since 1.0.0
 * @see AIAssessmentConstants
 */
public class AIAssessmentValidationService {
    
    /**
     * Validates and constrains an AI-suggested score against the deterministic baseline.
     *
     * <p><strong>Security Rules Applied</strong>:</p>
     * <ol>
     *   <li>AI cannot adjust more than ±{@value AIAssessmentConstants#MAX_AI_ADJUSTMENT} points from baseline</li>
     *   <li>AI cannot lower score if baseline is critical (>= 9.0)</li>
     *   <li>AI cannot raise more than {@value AIAssessmentConstants#MAX_SCORE_MULTIPLIER}x the baseline (prevents panic scoring)</li>
     *   <li>Final score is clamped to valid CVSS range [0.0, 10.0]</li>
     * </ol>
     *
     * <p><strong>Rationale</strong>: These constraints prevent AI hallucinations from producing
     * unreasonable scores while still allowing AI to refine the deterministic assessment.</p>
     *
     * @param aiSuggestedScore the AI-suggested score to validate
     * @param baselineScore the deterministically calculated baseline score
     * @return the validated and constrained score
     * @throws NullPointerException if any parameter is null
     */
    public SeverityScore validateAndConstrainScore(SeverityScore aiSuggestedScore,
                                                    SeverityScore baselineScore) {
        double ai = aiSuggestedScore.value();
        double baseline = baselineScore.value();

        double maxAllowed = baseline + MAX_AI_ADJUSTMENT;
        double minAllowed = baseline - MAX_AI_ADJUSTMENT;

        if (ai > maxAllowed) {
            ai = maxAllowed;
        } else if (ai < minAllowed) {
            ai = minAllowed;
        }

        if (baselineScore.isCritical() && ai < baseline) {
            ai = baseline;
        }

        double maxDouble = baseline * MAX_SCORE_MULTIPLIER;
        if (ai > maxDouble) {
            ai = maxDouble;
        }

        ai = Math.min(SeverityScore.MAX_SCORE, Math.max(SeverityScore.MIN_SCORE, ai));

        return new SeverityScore(ai);
    }
    
    /**
     * Determines if AI analysis is reliable based on reported confidence level.
     *
     * <p>Minimum acceptable confidence is {@value AIAssessmentConstants#MIN_CONFIDENCE_ACCEPTABLE}.
     * Below this threshold, the AI analysis is considered unreliable and will be rejected.</p>
     *
     * @param confidenceLevel the confidence level (0.0 to 1.0)
     * @return true if confidence is acceptable (>= {@value AIAssessmentConstants#MIN_CONFIDENCE_ACCEPTABLE})
     */
    public boolean isConfidenceAcceptable(double confidenceLevel) {
        return confidenceLevel >= MIN_CONFIDENCE_ACCEPTABLE;
    }
    
    /**
     * Determines if AI analysis should be rejected and use only baseline scoring.
     *
     * <p><strong>Rejection Criteria</strong>:</p>
     * <ul>
     *   <li>Confidence < {@value AIAssessmentConstants#MIN_CONFIDENCE_ACCEPTABLE} (unreliable)</li>
     *   <li>Suggested adjustment > {@value AIAssessmentConstants#MAX_ADJUSTMENT_BEFORE_REJECTION} points (potential hallucination)</li>
     *   <li>Justification empty or too short (< {@value AIAssessmentConstants#FALLBACK_MIN_JUSTIFICATION_LENGTH} characters)</li>
     * </ul>
     *
     * <p><strong>Rationale</strong>: These checks protect against low-quality AI outputs that
     * could mislead security analysis.</p>
     *
     * @param aiScore the AI-suggested score
     * @param baselineScore the deterministic baseline score
     * @param confidenceLevel the confidence level (0.0 to 1.0)
     * @param justification the AI's justification text
     * @return true if analysis should be rejected
     * @throws NullPointerException if aiScore or baselineScore is null
     */
    public boolean shouldRejectAIAnalysis(SeverityScore aiScore,
                                         SeverityScore baselineScore,
                                         double confidenceLevel,
                                         String justification) {
        if (confidenceLevel < MIN_CONFIDENCE_ACCEPTABLE) {
            return true;
        }

        double adjustment = Math.abs(aiScore.value() - baselineScore.value());
        if (adjustment > MAX_ADJUSTMENT_BEFORE_REJECTION) {
            return true;
        }

        if (justification == null || justification.length() < FALLBACK_MIN_JUSTIFICATION_LENGTH) {
            return true;
        }

        return false;
    }
    
    /**
     * Blends AI score with baseline using confidence-based weighting.
     *
     * <p><strong>Blending Formula</strong>:</p>
     * <pre>
     * finalScore = (baseline * (1 - aiWeight)) + (aiScore * aiWeight)
     *
     * where:
     *   aiWeight = min(confidence, {@value AIAssessmentConstants#MAX_AI_WEIGHT})
     * </pre>
     *
     * <p><strong>Rationale</strong>:</p>
     * <ul>
     *   <li>Low confidence: more weight to baseline (safer, deterministic)</li>
     *   <li>High confidence: more weight to AI (better contextual understanding)</li>
     *   <li>Maximum AI weight is {@value AIAssessmentConstants#MAX_AI_WEIGHT}: baseline always has >=30% influence</li>
     * </ul>
     *
     * @param aiScore the AI-suggested score
     * @param baselineScore the deterministic baseline score
     * @param confidenceLevel the confidence level (0.0 to 1.0)
     * @return the blended score weighted by confidence
     * @throws NullPointerException if any score parameter is null
     */
    public SeverityScore blendScores(SeverityScore aiScore,
                                     SeverityScore baselineScore,
                                     double confidenceLevel) {
        double aiWeight = Math.min(MAX_AI_WEIGHT, confidenceLevel);
        double baselineWeight = 1.0 - aiWeight;

        double blended = (baselineScore.value() * baselineWeight) +
                        (aiScore.value() * aiWeight);

        return new SeverityScore(blended);
    }
    
    /**
     * Determines if the analysis requires human review.
     *
     * <p><strong>Review is Required When</strong>:</p>
     * <ul>
     *   <li>Confidence < {@value AIAssessmentConstants#MIN_CONFIDENCE_FOR_NO_REVIEW} (moderate-low confidence)</li>
     *   <li>Final score is critical (>= 9.0) - always requires expert confirmation</li>
     *   <li>Large discrepancy between AI and baseline (> {@value AIAssessmentConstants#MIN_DISCREPANCY_FOR_REVIEW} points)</li>
     * </ul>
     *
     * <p><strong>Rationale</strong>: Human review provides a safety net for high-stakes decisions
     * and cases where AI and deterministic models significantly disagree.</p>
     *
     * @param finalScore the final blended score
     * @param aiScore the AI-suggested score
     * @param baselineScore the deterministic baseline score
     * @param confidenceLevel the confidence level (0.0 to 1.0)
     * @return true if human review is required
     * @throws NullPointerException if any score parameter is null
     */
    public boolean requiresHumanReview(SeverityScore finalScore,
                                      SeverityScore aiScore,
                                      SeverityScore baselineScore,
                                      double confidenceLevel) {
        if (confidenceLevel < MIN_CONFIDENCE_FOR_NO_REVIEW) {
            return true;
        }

        if (finalScore.isCritical()) {
            return true;
        }

        double discrepancy = Math.abs(aiScore.value() - baselineScore.value());
        if (discrepancy > MIN_DISCREPANCY_FOR_REVIEW) {
            return true;
        }

        return false;
    }
}


