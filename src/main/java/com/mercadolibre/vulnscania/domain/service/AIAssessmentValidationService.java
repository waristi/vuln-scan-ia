package com.mercadolibre.vulnscania.domain.service;

import com.mercadolibre.vulnscania.domain.model.vulnerability.SeverityScore;

import static com.mercadolibre.vulnscania.infrastructure.adapter.output.ai.AIAnalysisConstants.*;

/**
 * Domain Service: AI Assessment Validation Service
 * 
 * <p>Validates and controls AI analysis results to prevent "hallucinations" and ensure consistency.
 * This service implements critical business rules for safe use of AI in vulnerability assessment.</p>
 * 
 * <p><strong>Core Responsibilities</strong>:</p>
 * <ul>
 *   <li>Validate AI-suggested scores against deterministic baselines</li>
 *   <li>Prevent AI from making unreasonable adjustments</li>
 *   <li>Blend AI and baseline scores based on confidence</li>
 *   <li>Determine when human review is required</li>
 * </ul>
 * 
 * <p><strong>Key Business Rules</strong>:</p>
 * <ul>
 *   <li>AI cannot adjust more than ±{@value com.mercadolibre.vulnscania.infrastructure.adapter.output.ai.AIAnalysisConstants#MAX_AI_ADJUSTMENT} points from baseline</li>
 *   <li>AI cannot reduce critical scores (>= 9.0)</li>
 *   <li>AI gets maximum {@value com.mercadolibre.vulnscania.infrastructure.adapter.output.ai.AIAnalysisConstants#MAX_AI_WEIGHT} weight when blending scores</li>
 *   <li>Low confidence (<{@value com.mercadolibre.vulnscania.infrastructure.adapter.output.ai.AIAnalysisConstants#MIN_CONFIDENCE_ACCEPTABLE}) triggers rejection</li>
 *   <li>Large discrepancies (>{@value com.mercadolibre.vulnscania.infrastructure.adapter.output.ai.AIAnalysisConstants#MIN_DISCREPANCY_FOR_REVIEW}) require human review</li>
 * </ul>
 * 
 * <p>This is a DOMAIN SERVICE because it contains business logic that doesn't naturally
 * belong to any single entity but coordinates between Vulnerability and AI analysis.</p>
 */
public class AIAssessmentValidationService {
    
    /**
     * Validates and constrains an AI-suggested score against the deterministic baseline.
     * 
     * <p><strong>Security Rules Applied</strong>:</p>
     * <ol>
     *   <li>AI cannot adjust more than ±{@value com.mercadolibre.vulnscania.infrastructure.adapter.output.ai.AIAnalysisConstants#MAX_AI_ADJUSTMENT} points from baseline</li>
     *   <li>AI cannot lower score if baseline is critical (>= 9.0)</li>
     *   <li>AI cannot raise more than {@value com.mercadolibre.vulnscania.infrastructure.adapter.output.ai.AIAnalysisConstants#MAX_SCORE_MULTIPLIER}x the baseline (prevents "panic scoring")</li>
     *   <li>Final score is clamped to valid CVSS range [0.0, 10.0]</li>
     * </ol>
     * 
     * <p><strong>Rationale</strong>: These constraints prevent AI "hallucinations" from producing
     * unreasonable scores while still allowing AI to refine the deterministic assessment.</p>
     * 
     * @param aiSuggestedScore AI-suggested score
     * @param baselineScore deterministically calculated baseline score
     * @return validated and constrained score
     */
    public SeverityScore validateAndConstrainScore(SeverityScore aiSuggestedScore, 
                                                    SeverityScore baselineScore) {
        double ai = aiSuggestedScore.value();
        double baseline = baselineScore.value();
        
        // Rule 1: Do not allow adjustments greater than ±MAX_AI_ADJUSTMENT
        double maxAllowed = baseline + MAX_AI_ADJUSTMENT;
        double minAllowed = baseline - MAX_AI_ADJUSTMENT;
        
        if (ai > maxAllowed) {
            ai = maxAllowed;
        } else if (ai < minAllowed) {
            ai = minAllowed;
        }
        
        // Rule 2: Do not lower score if baseline is critical
        if (baselineScore.isCritical() && ai < baseline) {
            ai = baseline;
        }
        
        // Rule 3: Do not raise more than MAX_SCORE_MULTIPLIER x baseline (prevent "panic scoring")
        double maxDouble = baseline * MAX_SCORE_MULTIPLIER;
        if (ai > maxDouble) {
            ai = maxDouble;
        }
        
        // Clamp to valid CVSS range
        ai = Math.min(SeverityScore.MAX_SCORE, Math.max(SeverityScore.MIN_SCORE, ai));
        
        return new SeverityScore(ai);
    }
    
    /**
     * Determines if AI analysis is reliable based on reported confidence level.
     * 
     * <p>Minimum acceptable confidence is {@value com.mercadolibre.vulnscania.infrastructure.adapter.output.ai.AIAnalysisConstants#MIN_CONFIDENCE_ACCEPTABLE}.
     * Below this threshold, the AI analysis is considered unreliable and will be rejected.</p>
     * 
     * @param confidenceLevel confidence level (0.0 - 1.0)
     * @return true if confidence is acceptable (>= {@value com.mercadolibre.vulnscania.infrastructure.adapter.output.ai.AIAnalysisConstants#MIN_CONFIDENCE_ACCEPTABLE})
     */
    public boolean isConfidenceAcceptable(double confidenceLevel) {
        return confidenceLevel >= MIN_CONFIDENCE_ACCEPTABLE;
    }
    
    /**
     * Determines if AI analysis should be rejected and use only baseline scoring.
     * 
     * <p><strong>Rejection Criteria</strong>:</p>
     * <ul>
     *   <li>Confidence < {@value com.mercadolibre.vulnscania.infrastructure.adapter.output.ai.AIAnalysisConstants#MIN_CONFIDENCE_ACCEPTABLE} (unreliable)</li>
     *   <li>Suggested adjustment > {@value com.mercadolibre.vulnscania.infrastructure.adapter.output.ai.AIAnalysisConstants#MAX_ADJUSTMENT_BEFORE_REJECTION} points (potential hallucination)</li>
     *   <li>Justification empty or too short (< {@value com.mercadolibre.vulnscania.infrastructure.adapter.output.ai.AIAnalysisConstants#FALLBACK_MIN_JUSTIFICATION_LENGTH} characters)</li>
     * </ul>
     * 
     * <p><strong>Rationale</strong>: These checks protect against low-quality AI outputs that
     * could mislead security analysis.</p>
     * 
     * @param aiScore AI-suggested score
     * @param baselineScore deterministic baseline score
     * @param confidenceLevel confidence level (0.0 - 1.0)
     * @param justification AI's justification text
     * @return true if analysis should be rejected
     */
    public boolean shouldRejectAIAnalysis(SeverityScore aiScore,
                                         SeverityScore baselineScore,
                                         double confidenceLevel,
                                         String justification) {
        // Very low confidence
        if (confidenceLevel < MIN_CONFIDENCE_ACCEPTABLE) {
            return true;
        }
        
        // Adjustment too large (potential hallucination)
        double adjustment = Math.abs(aiScore.value() - baselineScore.value());
        if (adjustment > MAX_ADJUSTMENT_BEFORE_REJECTION) {
            return true;
        }
        
        // Insufficient justification
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
     *   aiWeight = min(confidence, {@value com.mercadolibre.vulnscania.infrastructure.adapter.output.ai.AIAnalysisConstants#MAX_AI_WEIGHT})
     * </pre>
     * 
     * <p><strong>Rationale</strong>:</p>
     * <ul>
     *   <li>Low confidence: more weight to baseline (safer, deterministic)</li>
     *   <li>High confidence: more weight to AI (better contextual understanding)</li>
     *   <li>Maximum AI weight is {@value com.mercadolibre.vulnscania.infrastructure.adapter.output.ai.AIAnalysisConstants#MAX_AI_WEIGHT}: baseline always has >=30% influence</li>
     * </ul>
     * 
     * @param aiScore AI-suggested score
     * @param baselineScore deterministic baseline score
     * @param confidenceLevel confidence level (0.0 - 1.0)
     * @return blended score weighted by confidence
     */
    public SeverityScore blendScores(SeverityScore aiScore,
                                     SeverityScore baselineScore,
                                     double confidenceLevel) {
        // Low confidence: more weight to baseline
        // High confidence: more weight to AI
        double aiWeight = Math.min(MAX_AI_WEIGHT, confidenceLevel); // Maximum MAX_AI_WEIGHT% weight to AI
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
     *   <li>Confidence < {@value com.mercadolibre.vulnscania.infrastructure.adapter.output.ai.AIAnalysisConstants#MIN_CONFIDENCE_FOR_NO_REVIEW} (moderate-low confidence)</li>
     *   <li>Final score is critical (>= 9.0) - always requires expert confirmation</li>
     *   <li>Large discrepancy between AI and baseline (> {@value com.mercadolibre.vulnscania.infrastructure.adapter.output.ai.AIAnalysisConstants#MIN_DISCREPANCY_FOR_REVIEW} points)</li>
     * </ul>
     * 
     * <p><strong>Rationale</strong>: Human review provides a safety net for high-stakes decisions
     * and cases where AI and deterministic models significantly disagree.</p>
     * 
     * @param finalScore final blended score
     * @param aiScore AI-suggested score
     * @param baselineScore deterministic baseline score
     * @param confidenceLevel confidence level (0.0 - 1.0)
     * @return true if human review is required
     */
    public boolean requiresHumanReview(SeverityScore finalScore,
                                      SeverityScore aiScore,
                                      SeverityScore baselineScore,
                                      double confidenceLevel) {
        // Moderate-low confidence
        if (confidenceLevel < MIN_CONFIDENCE_FOR_NO_REVIEW) {
            return true;
        }
        
        // Critical score always requires confirmation
        if (finalScore.isCritical()) {
            return true;
        }
        
        // Large discrepancy between AI and baseline
        double discrepancy = Math.abs(aiScore.value() - baselineScore.value());
        if (discrepancy > MIN_DISCREPANCY_FOR_REVIEW) {
            return true;
        }
        
        return false;
    }
}


