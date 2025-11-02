package com.mercadolibre.vulnscania.domain.port.output;

import com.mercadolibre.vulnscania.domain.model.application.ApplicationId;
import com.mercadolibre.vulnscania.domain.model.assessment.AssessmentId;
import com.mercadolibre.vulnscania.domain.model.assessment.AssessmentStatus;
import com.mercadolibre.vulnscania.domain.model.assessment.VulnerabilityAssessment;
import com.mercadolibre.vulnscania.domain.model.vulnerability.VulnerabilityId;

import java.util.List;
import java.util.Optional;

/**
 * Output Port: AssessmentRepository
 * Define el contrato para persistencia de evaluaciones.
 */
public interface AssessmentRepository {
    
    VulnerabilityAssessment save(VulnerabilityAssessment assessment);
    
    Optional<VulnerabilityAssessment> findById(AssessmentId id);
    
    List<VulnerabilityAssessment> findByVulnerabilityId(VulnerabilityId vulnerabilityId);
    
    List<VulnerabilityAssessment> findByApplicationId(ApplicationId applicationId);
    
    List<VulnerabilityAssessment> findByStatus(AssessmentStatus status);
    
    List<VulnerabilityAssessment> findRequiringReview();
    
    void deleteById(AssessmentId id);
}

