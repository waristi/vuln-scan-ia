package com.mercadolibre.vulnscania.domain.service;

import com.mercadolibre.vulnscania.domain.model.vulnerability.SeverityScore;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.*;

class AIAssessmentValidationServiceTest {
    
    private AIAssessmentValidationService service;
    
    @BeforeEach
    void setUp() {
        service = new AIAssessmentValidationService();
    }
    
    @Test
    void shouldRejectAIAnalysisWithLowConfidence() {
        // Given
        SeverityScore aiScore = new SeverityScore(8.0);
        SeverityScore baseline = new SeverityScore(7.0);
        double lowConfidence = 0.3;
        String justification = "Some analysis";
        
        // When
        boolean shouldReject = service.shouldRejectAIAnalysis(
            aiScore, baseline, lowConfidence, justification);
        
        // Then
        assertThat(shouldReject).isTrue();
    }
    
    @Test
    void shouldRejectAIAnalysisWithEmptyJustification() {
        // Given
        SeverityScore aiScore = new SeverityScore(8.0);
        SeverityScore baseline = new SeverityScore(7.0);
        double confidence = 0.8;
        String emptyJustification = "";
        
        // When
        boolean shouldReject = service.shouldRejectAIAnalysis(
            aiScore, baseline, confidence, emptyJustification);
        
        // Then
        assertThat(shouldReject).isTrue();
    }
    
    @Test
    void shouldRejectAIAnalysisWithExcessiveDeviation() {
        // Given
        SeverityScore aiScore = new SeverityScore(10.0);
        SeverityScore baseline = new SeverityScore(5.0);  // 5 points difference
        double confidence = 0.9;
        String justification = "Very high risk";
        
        // When
        boolean shouldReject = service.shouldRejectAIAnalysis(
            aiScore, baseline, confidence, justification);
        
        // Then
        assertThat(shouldReject).isTrue();
    }
    
    @Test
    void shouldAcceptValidAIAnalysis() {
        // Given
        SeverityScore aiScore = new SeverityScore(8.0);
        SeverityScore baseline = new SeverityScore(7.0);
        SeverityScore finalScore = new SeverityScore(7.8);  // Blended, not critical
        double confidence = 0.85;
        // Justification must be >= 50 characters
        String justification = "Detailed analysis of the vulnerability impact on the production environment with sensitive data exposure risks";
        
        // When
        boolean shouldReject = service.shouldRejectAIAnalysis(
            aiScore, baseline, confidence, justification);
        
        // Then - Should NOT reject: good confidence (0.85), small deviation (1.0), adequate justification (>50 chars)
        assertThat(shouldReject).isFalse();
    }
    
    @Test
    void shouldConstrainScoreToMaxDeviation() {
        // Given
        SeverityScore aiScore = new SeverityScore(10.0);
        SeverityScore baseline = new SeverityScore(5.0);
        
        // When
        SeverityScore constrained = service.validateAndConstrainScore(aiScore, baseline);
        
        // Then - Should be constrained to baseline + max deviation (3.0)
        assertThat(constrained.value()).isLessThanOrEqualTo(8.0);
    }
    
    @Test
    void shouldConstrainScoreToMinDeviation() {
        // Given
        SeverityScore aiScore = new SeverityScore(2.0);
        SeverityScore baseline = new SeverityScore(7.0);
        
        // When
        SeverityScore constrained = service.validateAndConstrainScore(aiScore, baseline);
        
        // Then - Should be constrained to baseline - max deviation (3.0)
        assertThat(constrained.value()).isGreaterThanOrEqualTo(4.0);
    }
    
    @Test
    void shouldNotConstrainScoreWithinValidRange() {
        // Given
        SeverityScore aiScore = new SeverityScore(7.5);
        SeverityScore baseline = new SeverityScore(7.0);
        
        // When
        SeverityScore constrained = service.validateAndConstrainScore(aiScore, baseline);
        
        // Then
        assertThat(constrained.value()).isEqualTo(7.5);
    }
    
    @Test
    void shouldBlendScoresWithHighConfidence() {
        // Given
        SeverityScore aiScore = new SeverityScore(8.0);
        SeverityScore baseline = new SeverityScore(7.0);
        double highConfidence = 0.9;
        
        // When
        SeverityScore blended = service.blendScores(aiScore, baseline, highConfidence);
        
        // Then - Should be closer to AI score (90% AI + 10% baseline = 7.9)
        // Using tolerance for floating point precision
        assertThat(blended.value()).isCloseTo(7.9, within(0.2));
    }
    
    @Test
    void shouldBlendScoresWithLowConfidence() {
        // Given
        SeverityScore aiScore = new SeverityScore(8.0);
        SeverityScore baseline = new SeverityScore(7.0);
        double lowConfidence = 0.5;
        
        // When
        SeverityScore blended = service.blendScores(aiScore, baseline, lowConfidence);
        
        // Then - Should be 50/50 blend (0.5 * 8.0 + 0.5 * 7.0 = 7.5)
        // Using tolerance for floating point precision
        assertThat(blended.value()).isCloseTo(7.5, within(0.2));
    }
    
    @Test
    void shouldRequireHumanReviewForLowConfidence() {
        // Given
        SeverityScore finalScore = new SeverityScore(8.0);
        SeverityScore aiScore = new SeverityScore(8.0);
        SeverityScore baseline = new SeverityScore(7.0);
        double lowConfidence = 0.4;
        
        // When
        boolean requiresReview = service.requiresHumanReview(
            finalScore, aiScore, baseline, lowConfidence);
        
        // Then
        assertThat(requiresReview).isTrue();
    }
    
    @Test
    void shouldRequireHumanReviewForCriticalScore() {
        // Given
        SeverityScore criticalScore = new SeverityScore(9.5);
        SeverityScore aiScore = new SeverityScore(9.5);
        SeverityScore baseline = new SeverityScore(9.0);
        double confidence = 0.9;
        
        // When
        boolean requiresReview = service.requiresHumanReview(
            criticalScore, aiScore, baseline, confidence);
        
        // Then
        assertThat(requiresReview).isTrue();
    }
    
    @Test
    void shouldRequireHumanReviewForLargeDeviation() {
        // Given
        SeverityScore finalScore = new SeverityScore(8.5);
        SeverityScore aiScore = new SeverityScore(8.5);
        SeverityScore baseline = new SeverityScore(5.0);  // Large deviation
        double confidence = 0.8;
        
        // When
        boolean requiresReview = service.requiresHumanReview(
            finalScore, aiScore, baseline, confidence);
        
        // Then
        assertThat(requiresReview).isTrue();
    }
    
    @Test
    void shouldNotRequireHumanReviewForNormalCase() {
        // Given
        SeverityScore finalScore = new SeverityScore(7.5);
        SeverityScore aiScore = new SeverityScore(7.5);
        SeverityScore baseline = new SeverityScore(7.0);
        double confidence = 0.85;
        
        // When
        boolean requiresReview = service.requiresHumanReview(
            finalScore, aiScore, baseline, confidence);
        
        // Then
        assertThat(requiresReview).isFalse();
    }
}

