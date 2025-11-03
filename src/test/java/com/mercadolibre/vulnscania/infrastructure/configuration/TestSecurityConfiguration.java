package com.mercadolibre.vulnscania.infrastructure.configuration;

import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Primary;
import org.springframework.context.annotation.Profile;
import org.springframework.core.annotation.Order;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;

/**
 * Test security configuration that disables authentication for integration tests.
 *
 * <p>This configuration is only active when the "test" profile is active,
 * allowing tests to run without JWT authentication while maintaining
 * security in production.</p>
 *
 * <p>The @Order(1) annotation ensures this configuration takes precedence
 * over the production SecurityConfiguration.</p>
 */
@TestConfiguration
@EnableWebSecurity
@Profile("test")
public class TestSecurityConfiguration {

    @Bean
    @Primary
    @Order(0)
    public SecurityFilterChain testSecurityFilterChain(HttpSecurity http) throws Exception {
        http
            .csrf(csrf -> csrf.disable())
            .sessionManagement(session -> 
                session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
            .authorizeHttpRequests(auth -> auth
                .anyRequest().permitAll()
            )
            .headers(headers -> headers.disable());
        return http.build();
    }
}

