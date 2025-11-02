package com.mercadolibre.vulnscania.domain.exception;

public class AssessmentAlreadyCompletedException extends DomainException {
    
    private final String assessmentId;
    
    public AssessmentAlreadyCompletedException(String assessmentId) {
        super("Assessment is already completed and cannot be modified: " + assessmentId);
        this.assessmentId = assessmentId;
    }
    
    @Override
    public String getErrorCode() {
        return "ASSESSMENT_ALREADY_COMPLETED";
    }
    
    public String getAssessmentId() {
        return assessmentId;
    }
}

