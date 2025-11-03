package com.mercadolibre.vulnscania.infrastructure.configuration;

import com.mercadolibre.vulnscania.application.port.input.LoginInputPort;
import com.mercadolibre.vulnscania.application.port.input.RefreshTokenInputPort;
import com.mercadolibre.vulnscania.application.port.input.RegisterUserInputPort;
import com.mercadolibre.vulnscania.application.usecase.LoginUseCase;
import com.mercadolibre.vulnscania.application.usecase.RefreshTokenUseCase;
import com.mercadolibre.vulnscania.application.usecase.RegisterUserUseCase;
import com.mercadolibre.vulnscania.domain.port.output.PasswordEncoderPort;
import com.mercadolibre.vulnscania.domain.port.output.TokenGeneratorPort;
import com.mercadolibre.vulnscania.domain.port.output.UserRepository;
import com.mercadolibre.vulnscania.domain.service.PasswordValidationService;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Configuration for authentication services (use cases).
 * 
 * <p>Creates and wires the authentication use cases with their dependencies,
 * following dependency injection principles.</p>
 */
@Configuration
public class AuthServiceConfiguration {
    
    @Bean
    public LoginInputPort loginUseCase(
            UserRepository userRepository,
            PasswordEncoderPort passwordEncoder,
            TokenGeneratorPort tokenGenerator) {
        return new LoginUseCase(userRepository, passwordEncoder, tokenGenerator);
    }
    
    @Bean
    public RefreshTokenInputPort refreshTokenUseCase(
            UserRepository userRepository,
            TokenGeneratorPort tokenGenerator) {
        return new RefreshTokenUseCase(userRepository, tokenGenerator);
    }
    
    @Bean
    public PasswordValidationService passwordValidationService() {
        return new PasswordValidationService();
    }

    @Bean
    public RegisterUserInputPort registerUserUseCase(
            UserRepository userRepository,
            PasswordEncoderPort passwordEncoder,
            PasswordValidationService passwordValidationService) {
        return new RegisterUserUseCase(userRepository, passwordEncoder, passwordValidationService);
    }
}

