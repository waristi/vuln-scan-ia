package com.mercadolibre.vulnscania.application.usecase;

import com.mercadolibre.vulnscania.application.port.input.RefreshTokenInputPort;
import com.mercadolibre.vulnscania.domain.command.RefreshTokenCommand;
import com.mercadolibre.vulnscania.domain.exception.InvalidTokenException;
import com.mercadolibre.vulnscania.domain.model.auth.AccessToken;
import com.mercadolibre.vulnscania.domain.model.auth.RefreshToken;
import com.mercadolibre.vulnscania.domain.model.auth.User;
import com.mercadolibre.vulnscania.domain.port.output.TokenGeneratorPort;
import com.mercadolibre.vulnscania.domain.port.output.UserRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Use Case: Refresh Access Token
 * 
 * <p>Orchestrates the token refresh process:
 * <ol>
 *   <li>Validates the refresh token</li>
 *   <li>Retrieves user information</li>
 *   <li>Generates new access token (1 hour expiration)</li>
 *   <li>Optionally generates new refresh token (8 hours expiration)</li>
 * </ol>
 * </p>
 */
public class RefreshTokenUseCase implements RefreshTokenInputPort {
    
    private static final Logger log = LoggerFactory.getLogger(RefreshTokenUseCase.class);
    
    private final UserRepository userRepository;
    private final TokenGeneratorPort tokenGenerator;
    
    public RefreshTokenUseCase(
            UserRepository userRepository,
            TokenGeneratorPort tokenGenerator) {
        this.userRepository = userRepository;
        this.tokenGenerator = tokenGenerator;
    }
    
    @Override
    public RefreshTokenInputPort.TokenRefreshResult execute(RefreshTokenCommand command) {
        log.debug("Attempting to refresh token");
        
        // Validate refresh token and extract user ID
        String userId = tokenGenerator.validateRefreshToken(command.refreshToken());
        if (userId == null) {
            log.warn("Invalid refresh token provided");
            throw new InvalidTokenException("Invalid or expired refresh token");
        }
        
        // Verify user exists and is active
        User user = userRepository.findById(com.mercadolibre.vulnscania.domain.model.auth.UserId.of(userId))
            .orElseThrow(() -> {
                log.warn("User not found for refresh token: {}", userId);
                return new InvalidTokenException("Invalid refresh token");
            });
        
        if (!user.isActive()) {
            log.warn("Attempt to refresh token for inactive user: {}", userId);
            throw new InvalidTokenException("User account is inactive");
        }
        
        // Generate new tokens
        AccessToken newAccessToken = tokenGenerator.generateAccessToken(userId);
        RefreshToken newRefreshToken = tokenGenerator.generateRefreshToken(userId);
        
        log.info("Successfully refreshed tokens for user: {}", userId);
        
        return new RefreshTokenInputPort.TokenRefreshResult(
            newAccessToken.value(),
            newRefreshToken.value(),
            user.getId().value(),
            user.getUsername(),
            user.getEmail()
        );
    }
}

