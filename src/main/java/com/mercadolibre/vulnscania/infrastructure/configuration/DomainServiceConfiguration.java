package com.mercadolibre.vulnscania.infrastructure.configuration;

import com.mercadolibre.vulnscania.application.usecase.EvaluateVulnerabilityUseCase;
import com.mercadolibre.vulnscania.application.usecase.GetVulnerabilityStatusUseCase;
import com.mercadolibre.vulnscania.application.usecase.RegisterApplicationUseCase;
import com.mercadolibre.vulnscania.domain.port.output.*;
import com.mercadolibre.vulnscania.domain.service.AIAssessmentValidationService;
import com.mercadolibre.vulnscania.infrastructure.adapter.output.ai.AIProviderSelector;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.function.Function;

/**
 * Configuration class to register Domain Services and Use Cases as Spring beans.
 * 
 * <p>This maintains the hexagonal architecture principle where the domain and
 * application layers remain free of framework dependencies. Both Domain Services
 * and Use Cases are pure classes without Spring annotations, and this configuration
 * class in the infrastructure layer handles their registration in the Spring context.</p>
 * 
 * <p>By registering these components here, we keep the application and domain layers
 * completely independent of the Spring Framework, which is a key principle of
 * hexagonal (ports and adapters) architecture.</p>
 * 
 * <p><strong>Rich Domain Model</strong>: Note that VulnerabilityScoringService has been
 * eliminated. The contextual scoring business logic now lives directly in the
 * Vulnerability aggregate, following DDD best practices and the "Tell, Don't Ask" principle.</p>
 */
@Configuration
public class DomainServiceConfiguration {
    
    // ========== Domain Services ==========
    
    /**
     * Registers the AIAssessmentValidationService as a Spring bean.
     * 
     * <p>This service validates and constrains AI-generated scores to prevent
     * hallucinations and ensure business rule compliance.</p>
     * 
     * <p>This is a true Domain Service because it coordinates validation logic
     * across multiple concerns (AI results, baseline scores, business rules) that
     * don't naturally belong to a single entity.</p>
     * 
     * @return AIAssessmentValidationService instance
     */
    @Bean
    public AIAssessmentValidationService aiAssessmentValidationService() {
        return new AIAssessmentValidationService();
    }
    
    // ========== Use Cases (Application Layer) ==========
    
    /**
     * Registers the EvaluateVulnerabilityUseCase as a Spring bean.
     * 
     * <p>This use case orchestrates the complete vulnerability evaluation process,
     * including CVE fetching, contextual scoring, AI analysis, and assessment creation.</p>
     * 
     * <p><strong>Architecture Note</strong>: The use case now receives a Function instead of
     * the AIProviderSelector directly. This maintains hexagonal architecture by keeping the
     * application layer independent of infrastructure implementation details. The Function
     * acts as an adapter between layers.</p>
     * 
     * <p>Note: VulnerabilityScoringService is no longer needed! The business logic
     * for contextual scoring now lives in the Vulnerability aggregate itself (Rich Domain Model).</p>
     * 
     * @param catalogPort port for fetching CVE data from NVD
     * @param applicationRepository repository for application data
     * @param vulnerabilityRepository repository for vulnerability data
     * @param assessmentRepository repository for assessment data
     * @param aiProviderSelector infrastructure service to resolve AI providers (wrapped in Function)
     * @param validationService service for validating AI results
     * @param eventPublisher publisher for domain events
     * @return EvaluateVulnerabilityUseCase instance
     */
    @Bean
    public EvaluateVulnerabilityUseCase evaluateVulnerabilityUseCase(
            VulnerabilityCatalogPort catalogPort,
            ApplicationRepository applicationRepository,
            VulnerabilityRepository vulnerabilityRepository,
            AssessmentRepository assessmentRepository,
            AIProviderSelector aiProviderSelector,
            AIAssessmentValidationService validationService,
            DomainEventPublisher eventPublisher) {
        
        // Wrap AIProviderSelector in a Function to decouple use case from infrastructure
        Function<String, AIAnalysisPort> aiProviderResolver = aiProviderSelector::getProvider;
        
        return new EvaluateVulnerabilityUseCase(
            catalogPort,
            applicationRepository,
            vulnerabilityRepository,
            assessmentRepository,
            aiProviderResolver,  // Pass function instead of infrastructure class
            validationService,
            eventPublisher
        );
    }
    
    /**
     * Registers the RegisterApplicationUseCase as a Spring bean.
     * 
     * <p>This use case handles the registration of new applications with their
     * dependencies and context information.</p>
     * 
     * @param applicationRepository repository for application data
     * @return RegisterApplicationUseCase instance
     */
    @Bean
    public RegisterApplicationUseCase registerApplicationUseCase(
            ApplicationRepository applicationRepository) {
        return new RegisterApplicationUseCase(applicationRepository);
    }
    
    /**
     * Registers the GetVulnerabilityStatusUseCase as a Spring bean.
     * 
     * <p>This use case retrieves the current status and details of a vulnerability.</p>
     * 
     * @param vulnerabilityRepository repository for vulnerability data
     * @return GetVulnerabilityStatusUseCase instance
     */
    @Bean
    public GetVulnerabilityStatusUseCase getVulnerabilityStatusUseCase(
            VulnerabilityRepository vulnerabilityRepository) {
        return new GetVulnerabilityStatusUseCase(vulnerabilityRepository);
    }
}

