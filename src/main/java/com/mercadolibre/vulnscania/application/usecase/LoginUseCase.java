package com.mercadolibre.vulnscania.application.usecase;

import com.mercadolibre.vulnscania.application.port.input.LoginInputPort;
import com.mercadolibre.vulnscania.domain.command.LoginCommand;
import com.mercadolibre.vulnscania.domain.exception.InvalidCredentialsException;
import com.mercadolibre.vulnscania.domain.model.auth.AccessToken;
import com.mercadolibre.vulnscania.domain.model.auth.RefreshToken;
import com.mercadolibre.vulnscania.domain.model.auth.User;
import com.mercadolibre.vulnscania.domain.port.output.PasswordEncoderPort;
import com.mercadolibre.vulnscania.domain.port.output.TokenGeneratorPort;
import com.mercadolibre.vulnscania.domain.port.output.UserRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Use Case: User Login
 * 
 * <p>Orchestrates the user authentication process:
 * <ol>
 *   <li>Validates user credentials</li>
 *   <li>Generates access token (1 hour expiration)</li>
 *   <li>Generates refresh token (8 hours expiration)</li>
 * </ol>
 * </p>
 */
public class LoginUseCase implements LoginInputPort {
    
    private static final Logger log = LoggerFactory.getLogger(LoginUseCase.class);
    
    private final UserRepository userRepository;
    private final PasswordEncoderPort passwordEncoder;
    private final TokenGeneratorPort tokenGenerator;
    
    public LoginUseCase(
            UserRepository userRepository,
            PasswordEncoderPort passwordEncoder,
            TokenGeneratorPort tokenGenerator) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.tokenGenerator = tokenGenerator;
    }
    
    @Override
    public LoginInputPort.LoginResult execute(LoginCommand command) {
        log.debug("Attempting login for user: {}", command.username());
        
        // Find user by username
        User user = userRepository.findByUsername(command.username())
            .orElseThrow(() -> {
                log.warn("Login attempt with non-existent username: {}", command.username());
                return new InvalidCredentialsException("Invalid username or password");
            });
        
        // Check if user is active
        if (!user.isActive()) {
            log.warn("Login attempt for inactive user: {}", command.username());
            throw new InvalidCredentialsException("User account is inactive");
        }
        
        // Validate password
        if (!passwordEncoder.matches(command.password(), user.getPasswordHash())) {
            log.warn("Invalid password for user: {}", command.username());
            throw new InvalidCredentialsException("Invalid username or password");
        }
        
        // Generate tokens
        AccessToken accessToken = tokenGenerator.generateAccessToken(user.getId().value());
        RefreshToken refreshToken = tokenGenerator.generateRefreshToken(user.getId().value());
        
        log.info("Successfully authenticated user: {}", command.username());
        
        return new LoginInputPort.LoginResult(
            accessToken.value(),
            refreshToken.value(),
            user.getId().value(),
            user.getUsername(),
            user.getEmail()
        );
    }
}

