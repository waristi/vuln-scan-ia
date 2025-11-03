package com.mercadolibre.vulnscania.domain.exception;

/**
 * Domain exception thrown when attempting to register a user that already exists.
 */
public class UserAlreadyExistsException extends DomainException {
    
    private final String username;
    
    public UserAlreadyExistsException(String message) {
        super(message);
        this.username = extractUsername(message);
    }
    
    public UserAlreadyExistsException(String message, Throwable cause) {
        super(message, cause);
        this.username = extractUsername(message);
    }
    
    @Override
    public String getErrorCode() {
        return "USER_ALREADY_EXISTS";
    }
    
    public String getUsername() {
        return username;
    }
    
    private String extractUsername(String message) {
        // Extract username from message like "Username already exists: username"
        if (message != null && message.contains(": ")) {
            return message.substring(message.lastIndexOf(": ") + 2);
        }
        return null;
    }
}

