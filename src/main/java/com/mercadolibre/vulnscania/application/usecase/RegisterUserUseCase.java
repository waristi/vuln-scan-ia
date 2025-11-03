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
 * Use Case for user registration.
 *
 * <p>Orchestrates the user registration process following these steps:</p>
 * <ol>
 *   <li>Validates username is not already taken</li>
 *   <li>Validates password strength</li>
 *   <li>Hashes the password</li>
 *   <li>Creates and saves the user</li>
 * </ol>
 *
 * <p>This is part of the application layer and orchestrates domain services
 * and repositories to fulfill the user registration business requirement.</p>
 *
 * @author Bernardo Zuluaga
 * @since 1.0.0
 */
public class RegisterUserUseCase implements RegisterUserInputPort {

    private static final Logger log = LoggerFactory.getLogger(RegisterUserUseCase.class);

    private final UserRepository userRepository;
    private final PasswordEncoderPort passwordEncoder;
    private final PasswordValidationService passwordValidationService;

    /**
     * Constructs a new RegisterUserUseCase.
     *
     * @param userRepository the user repository port
     * @param passwordEncoder the password encoder port
     * @param passwordValidationService the password validation service
     */
    public RegisterUserUseCase(
            UserRepository userRepository,
            PasswordEncoderPort passwordEncoder,
            PasswordValidationService passwordValidationService) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.passwordValidationService = passwordValidationService;
    }

    /**
     * Executes the user registration use case.
     *
     * @param command the registration command containing username, password, and email
     * @return the registration result with user ID, username, and email
     * @throws UserAlreadyExistsException if the username is already taken
     * @throws WeakPasswordException if the password does not meet strength requirements
     * @throws NullPointerException if command is null
     */
    @Override
    public RegisterUserInputPort.RegistrationResult execute(RegisterUserCommand command) {
        log.debug("Attempting to register user: {}", command.username());

        if (userRepository.existsByUsername(command.username())) {
            log.warn("Registration attempt with existing username: {}", command.username());
            throw new UserAlreadyExistsException("Username already exists: " + command.username());
        }

        passwordValidationService.validatePasswordStrength(command.password());

        String passwordHash = passwordEncoder.encode(command.password());

        User user = User.create(command.username(), passwordHash, command.email());

        User savedUser = userRepository.save(user);

        log.info("Successfully registered user: {}", command.username());

        return new RegisterUserInputPort.RegistrationResult(
            savedUser.getId().value(),
            savedUser.getUsername(),
            savedUser.getEmail()
        );
    }
}

