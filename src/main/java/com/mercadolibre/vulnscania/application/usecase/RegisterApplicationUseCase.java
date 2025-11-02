package com.mercadolibre.vulnscania.application.usecase;

import com.mercadolibre.vulnscania.domain.command.RegisterApplicationCommand;
import com.mercadolibre.vulnscania.domain.exception.ApplicationAlreadyExistsException;
import com.mercadolibre.vulnscania.domain.model.application.Application;
import com.mercadolibre.vulnscania.domain.port.output.ApplicationRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Use Case: Register Application
 * 
 * Registers a new application in the system with its context information.
 * Validates that application doesn't already exist.
 */
@Service
public class RegisterApplicationUseCase {
    
    private static final Logger log = LoggerFactory.getLogger(RegisterApplicationUseCase.class);
    
    private final ApplicationRepository applicationRepository;
    
    public RegisterApplicationUseCase(ApplicationRepository applicationRepository) {
        this.applicationRepository = applicationRepository;
    }
    
    @Transactional
    public ApplicationResult execute(RegisterApplicationCommand command) {
        log.info("Registering application: {}", command.name());
        
        // Validate uniqueness
        if (applicationRepository.existsByName(command.name())) {
            throw new ApplicationAlreadyExistsException(command.name());
        }
        
        // Create application
        Application application = Application.create(
            command.name(),
            command.techStack(),
            command.dependencies(),
            command.internetExposed(),
            command.dataSensitivity(),
            command.runtimeEnvironments()
        );
        
        // Save
        Application saved = applicationRepository.save(application);
        
        log.info("Application registered successfully: {} (ID: {})", 
            saved.getName(), saved.getId().value());
        
        return ApplicationResult.fromDomain(saved);
    }
    
    /**
     * Result DTO for the use case.
     */
    public record ApplicationResult(
        String id,
        String name,
        int dependenciesCount,
        boolean internetExposed,
        String dataSensitivity,
        double riskFactor,
        boolean isCriticalInfrastructure
    ) {
        public static ApplicationResult fromDomain(Application application) {
            return new ApplicationResult(
                application.getId().value(),
                application.getName(),
                application.getDependencies().size(),
                application.isInternetExposed(),
                application.getDataSensitivity().name(),
                application.calculateRiskFactor(),
                application.isCriticalInfrastructure()
            );
        }
    }
}

