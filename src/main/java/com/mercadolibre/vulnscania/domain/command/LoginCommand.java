package com.mercadolibre.vulnscania.domain.command;

import java.util.Objects;

/**
 * Command to authenticate a user and generate tokens.
 * 
 * <p>Immutable record following the Command Pattern.
 * Represents the user's intention to log in to the system.</p>
 * 
 * @param username User's username
 * @param password User's plain text password
 */
public record LoginCommand(String username, String password) {
    
    public LoginCommand {
        Objects.requireNonNull(username, "Username is required");
        Objects.requireNonNull(password, "Password is required");
        
        if (username.isBlank()) {
            throw new IllegalArgumentException("Username cannot be blank");
        }
        if (password.isBlank()) {
            throw new IllegalArgumentException("Password cannot be blank");
        }
    }
}

