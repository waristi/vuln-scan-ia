package com.mercadolibre.vulnscania.infrastructure.adapter.output.security;

import com.mercadolibre.vulnscania.domain.port.output.PasswordEncoderPort;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

/**
 * Infrastructure adapter that implements password encoding using BCrypt.
 * 
 * <p>Uses Spring Security's BCryptPasswordEncoder for secure password hashing.
 * BCrypt automatically handles salt generation and provides resistance against
 * rainbow table attacks.</p>
 */
@Component
public class BcryptPasswordEncoderAdapter implements PasswordEncoderPort {
    
    private final PasswordEncoder passwordEncoder;
    
    public BcryptPasswordEncoderAdapter() {
        this.passwordEncoder = new BCryptPasswordEncoder();
    }
    
    @Override
    public String encode(String rawPassword) {
        if (rawPassword == null || rawPassword.isBlank()) {
            throw new IllegalArgumentException("Raw password cannot be null or blank");
        }
        return passwordEncoder.encode(rawPassword);
    }
    
    @Override
    public boolean matches(String rawPassword, String encodedPassword) {
        if (rawPassword == null || rawPassword.isBlank()) {
            throw new IllegalArgumentException("Raw password cannot be null or blank");
        }
        if (encodedPassword == null || encodedPassword.isBlank()) {
            throw new IllegalArgumentException("Encoded password cannot be null or blank");
        }
        return passwordEncoder.matches(rawPassword, encodedPassword);
    }
}

