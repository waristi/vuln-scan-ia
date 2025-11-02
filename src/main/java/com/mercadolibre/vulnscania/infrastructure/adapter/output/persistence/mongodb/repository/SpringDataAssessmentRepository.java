package com.mercadolibre.vulnscania.infrastructure.adapter.output.persistence.mongodb.repository;

import com.mercadolibre.vulnscania.infrastructure.adapter.output.persistence.mongodb.document.AssessmentDocument;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.List;

/**
 * Spring Data MongoDB repository for AssessmentDocument.
 */
public interface SpringDataAssessmentRepository extends MongoRepository<AssessmentDocument, String> {
    
    List<AssessmentDocument> findByVulnerabilityId(String vulnerabilityId);
    
    List<AssessmentDocument> findByApplicationId(String applicationId);
    
    List<AssessmentDocument> findByStatus(String status);
}

