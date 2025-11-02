package com.mercadolibre.vulnscania.infrastructure.adapter.output.persistence.mongodb;

import com.mercadolibre.vulnscania.domain.model.application.Application;
import com.mercadolibre.vulnscania.domain.model.application.ApplicationId;
import com.mercadolibre.vulnscania.domain.model.assessment.AssessmentId;
import com.mercadolibre.vulnscania.domain.model.assessment.AssessmentStatus;
import com.mercadolibre.vulnscania.domain.model.assessment.VulnerabilityAssessment;
import com.mercadolibre.vulnscania.domain.model.vulnerability.Vulnerability;
import com.mercadolibre.vulnscania.domain.model.vulnerability.VulnerabilityId;
import com.mercadolibre.vulnscania.domain.port.output.ApplicationRepository;
import com.mercadolibre.vulnscania.domain.port.output.AssessmentRepository;
import com.mercadolibre.vulnscania.domain.port.output.VulnerabilityRepository;
import com.mercadolibre.vulnscania.infrastructure.adapter.output.persistence.mongodb.document.AssessmentDocument;
import com.mercadolibre.vulnscania.infrastructure.adapter.output.persistence.mongodb.repository.SpringDataAssessmentRepository;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * MongoDB adapter implementing AssessmentRepository port.
 * Requires VulnerabilityRepository and ApplicationRepository to reconstruct full aggregates.
 */
@Component
public class MongoAssessmentRepositoryAdapter implements AssessmentRepository {
    
    private final SpringDataAssessmentRepository springRepository;
    private final VulnerabilityRepository vulnerabilityRepository;
    private final ApplicationRepository applicationRepository;
    
    public MongoAssessmentRepositoryAdapter(
            SpringDataAssessmentRepository springRepository,
            VulnerabilityRepository vulnerabilityRepository,
            ApplicationRepository applicationRepository) {
        this.springRepository = springRepository;
        this.vulnerabilityRepository = vulnerabilityRepository;
        this.applicationRepository = applicationRepository;
    }
    
    @Override
    public VulnerabilityAssessment save(VulnerabilityAssessment assessment) {
        AssessmentDocument document = AssessmentDocument.fromDomain(assessment);
        springRepository.save(document);
        return assessment;
    }
    
    @Override
    public Optional<VulnerabilityAssessment> findById(AssessmentId id) {
        return springRepository.findById(id.value())
            .flatMap(this::toDomainWithAggregates);
    }
    
    @Override
    public List<VulnerabilityAssessment> findByVulnerabilityId(VulnerabilityId vulnerabilityId) {
        return springRepository.findByVulnerabilityId(vulnerabilityId.value()).stream()
            .map(this::toDomainWithAggregates)
            .filter(Optional::isPresent)
            .map(Optional::get)
            .collect(Collectors.toList());
    }
    
    @Override
    public List<VulnerabilityAssessment> findByApplicationId(ApplicationId applicationId) {
        return springRepository.findByApplicationId(applicationId.value()).stream()
            .map(this::toDomainWithAggregates)
            .filter(Optional::isPresent)
            .map(Optional::get)
            .collect(Collectors.toList());
    }
    
    @Override
    public List<VulnerabilityAssessment> findByStatus(AssessmentStatus status) {
        return springRepository.findByStatus(status.name()).stream()
            .map(this::toDomainWithAggregates)
            .filter(Optional::isPresent)
            .map(Optional::get)
            .collect(Collectors.toList());
    }
    
    @Override
    public List<VulnerabilityAssessment> findRequiringReview() {
        return springRepository.findByStatus(AssessmentStatus.REQUIRES_REVIEW.name()).stream()
            .map(this::toDomainWithAggregates)
            .filter(Optional::isPresent)
            .map(Optional::get)
            .collect(Collectors.toList());
    }
    
    @Override
    public void deleteById(AssessmentId id) {
        springRepository.deleteById(id.value());
    }
    
    /**
     * Converts document to domain by loading referenced aggregates.
     */
    private Optional<VulnerabilityAssessment> toDomainWithAggregates(AssessmentDocument document) {
        Optional<Vulnerability> vulnerability = vulnerabilityRepository.findById(
            VulnerabilityId.of(document.getVulnerabilityId())
        );
        Optional<Application> application = applicationRepository.findById(
            ApplicationId.of(document.getApplicationId())
        );
        
        if (vulnerability.isPresent() && application.isPresent()) {
            return Optional.of(document.toDomain(vulnerability.get(), application.get()));
        }
        return Optional.empty();
    }
}

