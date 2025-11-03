package com.mercadolibre.vulnscania.infrastructure.configuration;

import com.mercadolibre.vulnscania.infrastructure.adapter.input.security.JwtAuthenticationFilter;
import com.mercadolibre.vulnscania.domain.port.output.TokenGeneratorPort;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

/**
 * Security configuration for JWT-based authentication.
 * 
 * <p>Configures Spring Security to:
 * <ul>
 *   <li>Disable session management (stateless JWT authentication)</li>
 *   <li>Allow public endpoints (login, refresh, health, Swagger)</li>
 *   <li>Protect all other endpoints with JWT validation</li>
 *   <li>Add JWT authentication filter to the filter chain</li>
 * </ul>
 * </p>
 */
@Configuration
@EnableWebSecurity
@org.springframework.context.annotation.Profile("!test")
public class SecurityConfiguration {
    
    private final TokenGeneratorPort tokenValidator;
    
    public SecurityConfiguration(TokenGeneratorPort tokenValidator) {
        this.tokenValidator = tokenValidator;
    }
    
    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
            .csrf(csrf -> csrf.disable()) // Disable CSRF for stateless JWT
            .sessionManagement(session -> 
                session.sessionCreationPolicy(SessionCreationPolicy.STATELESS)) // Stateless JWT
            .authorizeHttpRequests(auth -> auth
                .requestMatchers("/api/v1/auth/**").permitAll() // Public auth endpoints (register, login, refresh)
                .requestMatchers("/actuator/health").permitAll() // Health check
                // Swagger endpoints - should be disabled in production via SWAGGER_ENABLED=false
                .requestMatchers("/swagger-ui/**", "/v3/api-docs/**", "/swagger-ui.html").permitAll()
                .anyRequest().authenticated() // All other endpoints require authentication
            )
            .addFilterBefore(
                new com.mercadolibre.vulnscania.infrastructure.configuration.SecurityHeadersConfiguration(),
                UsernamePasswordAuthenticationFilter.class
            )
            .addFilterBefore(
                new JwtAuthenticationFilter(tokenValidator),
                UsernamePasswordAuthenticationFilter.class
            )
            .headers(headers -> headers
                // Additional headers are handled by SecurityHeadersConfiguration filter
                .frameOptions(frame -> frame.deny())
                .contentTypeOptions(contentType -> contentType.disable())
            )
            .cors(cors -> cors.disable()); // CORS should be configured based on frontend requirements
        
        return http.build();
    }
}

