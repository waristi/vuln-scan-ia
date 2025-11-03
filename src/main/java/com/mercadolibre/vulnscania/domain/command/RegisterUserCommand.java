package com.mercadolibre.vulnscania.domain.command;

import java.util.Objects;

/**
 * Command to register a new user.
 * 
 * <p>Immutable record following the Command Pattern.
 * Represents the user's intention to create a new account.</p>
 * 
 * @param username Unique username
 * @param password Plain text password (will be hashed)
 * @param email User email
 */
public record RegisterUserCommand(String username, String password, String email) {
    
    public RegisterUserCommand {
        Objects.requireNonNull(username, "Username is required");
        Objects.requireNonNull(password, "Password is required");
        Objects.requireNonNull(email, "Email is required");
        
        if (username.isBlank()) {
            throw new IllegalArgumentException("Username cannot be blank");
        }
        if (password.isBlank()) {
            throw new IllegalArgumentException("Password cannot be blank");
        }
        if (email.isBlank()) {
            throw new IllegalArgumentException("Email cannot be blank");
        }
    }
}

