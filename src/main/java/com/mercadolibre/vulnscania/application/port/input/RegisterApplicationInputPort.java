package com.mercadolibre.vulnscania.application.port.input;

import com.mercadolibre.vulnscania.application.usecase.RegisterApplicationUseCase;
import com.mercadolibre.vulnscania.domain.command.RegisterApplicationCommand;

/**
 * Input Port for application registration use case.
 * 
 * <p>This interface represents the entry point to the application layer for
 * application registration operations. It follows the hexagonal architecture
 * principle by defining a port (interface) that can be implemented by use cases
 * and consumed by adapters (such as REST controllers).</p>
 * 
 * <p>By depending on this interface rather than the concrete use case class,
 * we achieve:</p>
 * <ul>
 *   <li>Dependency Inversion Principle (SOLID)</li>
 *   <li>Better testability (easy to mock)</li>
 *   <li>Clear architectural boundaries</li>
 *   <li>Framework independence</li>
 * </ul>
 */
public interface RegisterApplicationInputPort {
    
    /**
     * Registers a new application in the system.
     * 
     * @param command the registration command with application details and dependencies
     * @return the application result with generated ID and metadata
     */
    RegisterApplicationUseCase.ApplicationResult execute(RegisterApplicationCommand command);
}

