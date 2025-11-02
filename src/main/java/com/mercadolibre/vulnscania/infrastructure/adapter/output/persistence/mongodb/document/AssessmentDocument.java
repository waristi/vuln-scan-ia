package com.mercadolibre.vulnscania.infrastructure.adapter.output.persistence.mongodb.document;

import com.mercadolibre.vulnscania.domain.model.application.Application;
import com.mercadolibre.vulnscania.domain.model.assessment.AssessmentId;
import com.mercadolibre.vulnscania.domain.model.assessment.AssessmentStatus;
import com.mercadolibre.vulnscania.domain.model.assessment.VulnerabilityAssessment;
import com.mercadolibre.vulnscania.domain.model.vulnerability.SeverityScore;
import com.mercadolibre.vulnscania.domain.model.vulnerability.Vulnerability;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.Instant;

/**
 * MongoDB document for VulnerabilityAssessment aggregate.
 * Stores references to Vulnerability and Application by ID.
 */
@Document(collection = "assessments")
public class AssessmentDocument {
    
    @Id
    private String id;
    
    @Indexed
    private String vulnerabilityId;
    
    @Indexed
    private String applicationId;
    
    @Indexed
    private String status;
    
    private Double finalScore;
    private String justification;
    private String aiAnalysis;
    private Double confidenceLevel;
    private Instant startedAt;
    private Instant completedAt;
    
    public AssessmentDocument() {
    }
    
    /**
     * Converts domain Assessment to document.
     * Note: Does not store full Vulnerability/Application, only IDs.
     */
    public static AssessmentDocument fromDomain(VulnerabilityAssessment assessment) {
        AssessmentDocument doc = new AssessmentDocument();
        doc.id = assessment.getId().value();
        doc.vulnerabilityId = assessment.getVulnerability().getId().value();
        doc.applicationId = assessment.getApplication().getId().value();
        doc.status = assessment.getStatus().name();
        doc.finalScore = assessment.getFinalScore() != null ? 
            assessment.getFinalScore().value() : null;
        doc.justification = assessment.getJustification();
        doc.aiAnalysis = assessment.getAiAnalysis();
        doc.confidenceLevel = assessment.getConfidenceLevel();
        doc.startedAt = assessment.getStartedAt();
        doc.completedAt = assessment.getCompletedAt();
        return doc;
    }
    
    /**
     * Converts document to domain Assessment.
     * Requires Vulnerability and Application to be provided separately.
     */
    public VulnerabilityAssessment toDomain(Vulnerability vulnerability, Application application) {
        SeverityScore finalScore = this.finalScore != null ? 
            new SeverityScore(this.finalScore) : null;
        
        return VulnerabilityAssessment.reconstitute(
            AssessmentId.of(id),
            vulnerability,
            application,
            AssessmentStatus.valueOf(status),
            finalScore,
            justification,
            aiAnalysis,
            confidenceLevel,
            startedAt,
            completedAt
        );
    }
    
    public String getId() {
        return id;
    }
    
    public String getVulnerabilityId() {
        return vulnerabilityId;
    }
    
    public String getApplicationId() {
        return applicationId;
    }
    
    public String getStatus() {
        return status;
    }
}

