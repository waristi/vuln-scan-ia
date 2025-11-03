package com.mercadolibre.vulnscania.domain.exception;

/**
 * Domain exception thrown when user credentials are invalid.
 * 
 * <p>This exception is thrown during login when:
 * <ul>
 *   <li>Username doesn't exist</li>
 *   <li>Password doesn't match</li>
 *   <li>User account is inactive</li>
 * </ul>
 * </p>
 */
public class InvalidCredentialsException extends DomainException {
    
    public InvalidCredentialsException(String message) {
        super(message);
    }
    
    public InvalidCredentialsException(String message, Throwable cause) {
        super(message, cause);
    }
    
    @Override
    public String getErrorCode() {
        return "INVALID_CREDENTIALS";
    }
}

