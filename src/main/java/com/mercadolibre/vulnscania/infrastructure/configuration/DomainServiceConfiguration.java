package com.mercadolibre.vulnscania.infrastructure.configuration;

import com.mercadolibre.vulnscania.domain.service.AIAssessmentValidationService;
import com.mercadolibre.vulnscania.domain.service.VulnerabilityScoringService;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Configuration class to register Domain Services as Spring beans.
 * 
 * <p>This maintains the hexagonal architecture principle where the domain layer
 * remains free of framework dependencies. Domain Services are pure domain logic
 * classes without Spring annotations, and this configuration class in the
 * infrastructure layer handles their registration in the Spring context.</p>
 * 
 * @see VulnerabilityScoringService
 * @see AIAssessmentValidationService
 */
@Configuration
public class DomainServiceConfiguration {
    
    /**
     * Registers the VulnerabilityScoringService as a Spring bean.
     * 
     * <p>This service encapsulates business logic for calculating contextual
     * vulnerability scores based on application context.</p>
     * 
     * @return VulnerabilityScoringService instance
     */
    @Bean
    public VulnerabilityScoringService vulnerabilityScoringService() {
        return new VulnerabilityScoringService();
    }
    
    /**
     * Registers the AIAssessmentValidationService as a Spring bean.
     * 
     * <p>This service validates and constrains AI-generated scores to prevent
     * hallucinations and ensure business rule compliance.</p>
     * 
     * @return AIAssessmentValidationService instance
     */
    @Bean
    public AIAssessmentValidationService aiAssessmentValidationService() {
        return new AIAssessmentValidationService();
    }
}

