package com.mercadolibre.vulnscania.application.usecase;

import com.mercadolibre.vulnscania.application.port.input.RegisterUserInputPort;
import com.mercadolibre.vulnscania.domain.command.RegisterUserCommand;
import com.mercadolibre.vulnscania.domain.exception.UserAlreadyExistsException;
import com.mercadolibre.vulnscania.domain.model.auth.User;
import com.mercadolibre.vulnscania.domain.port.output.PasswordEncoderPort;
import com.mercadolibre.vulnscania.domain.port.output.UserRepository;
import com.mercadolibre.vulnscania.domain.service.PasswordValidationService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Use Case: Register User
 * 
 * <p>Orchestrates the user registration process:
 * <ol>
 *   <li>Validates username is not already taken</li>
 *   <li>Hashes the password</li>
 *   <li>Creates and saves the user</li>
 * </ol>
 * </p>
 */
public class RegisterUserUseCase implements RegisterUserInputPort {
    
    private static final Logger log = LoggerFactory.getLogger(RegisterUserUseCase.class);
    
    private final UserRepository userRepository;
    private final PasswordEncoderPort passwordEncoder;
    private final PasswordValidationService passwordValidationService;
    
    public RegisterUserUseCase(
            UserRepository userRepository,
            PasswordEncoderPort passwordEncoder,
            PasswordValidationService passwordValidationService) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.passwordValidationService = passwordValidationService;
    }
    
    @Override
    public RegisterUserInputPort.RegistrationResult execute(RegisterUserCommand command) {
        log.debug("Attempting to register user: {}", command.username());
        
        // Check if user already exists
        if (userRepository.existsByUsername(command.username())) {
            log.warn("Registration attempt with existing username: {}", command.username());
            throw new UserAlreadyExistsException("Username already exists: " + command.username());
        }
        
        // Validate password strength
        passwordValidationService.validatePasswordStrength(command.password());
        
        // Hash password
        String passwordHash = passwordEncoder.encode(command.password());
        
        // Create user
        User user = User.create(command.username(), passwordHash, command.email());
        
        // Save user
        User savedUser = userRepository.save(user);
        
        log.info("Successfully registered user: {}", command.username());
        
        return new RegisterUserInputPort.RegistrationResult(
            savedUser.getId().value(),
            savedUser.getUsername(),
            savedUser.getEmail()
        );
    }
}

