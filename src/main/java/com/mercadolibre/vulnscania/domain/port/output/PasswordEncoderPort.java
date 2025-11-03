package com.mercadolibre.vulnscania.domain.port.output;

/**
 * Output Port for password encoding and validation.
 * 
 * <p>Defines the contract for password hashing and verification, keeping the domain
 * layer independent of the specific hashing algorithm (BCrypt, Argon2, etc.).</p>
 */
public interface PasswordEncoderPort {
    
    /**
     * Encodes a plain text password into a hash.
     * 
     * @param rawPassword The plain text password to encode
     * @return The hashed password
     * @throws IllegalArgumentException if rawPassword is null or blank
     */
    String encode(String rawPassword);
    
    /**
     * Verifies if a raw password matches an encoded password hash.
     * 
     * @param rawPassword The plain text password to verify
     * @param encodedPassword The encoded password hash to compare against
     * @return true if passwords match, false otherwise
     * @throws IllegalArgumentException if either parameter is null or blank
     */
    boolean matches(String rawPassword, String encodedPassword);
}

