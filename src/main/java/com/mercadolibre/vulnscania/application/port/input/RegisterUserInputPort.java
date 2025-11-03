package com.mercadolibre.vulnscania.application.port.input;

import com.mercadolibre.vulnscania.domain.command.RegisterUserCommand;

/**
 * Input Port for user registration operations.
 * 
 * <p>Defines the interface for user registration use cases, following hexagonal architecture.
 * Controllers depend on this port rather than concrete use case implementations.</p>
 */
public interface RegisterUserInputPort {
    
    /**
     * Registers a new user in the system.
     * 
     * @param command Registration command with user details
     * @return Registration result with user information
     * @throws com.mercadolibre.vulnscania.domain.exception.UserAlreadyExistsException if username already exists
     */
    RegistrationResult execute(RegisterUserCommand command);
    
    /**
     * Result of user registration operation.
     * 
     * @param userId User identifier
     * @param username User's username
     * @param email User's email
     */
    record RegistrationResult(
        String userId,
        String username,
        String email
    ) {}
}

