package com.mercadolibre.vulnscania.domain.exception;

/**
 * Domain exception thrown when a token is invalid or expired.
 * 
 * <p>This exception is thrown when:
 * <ul>
 *   <li>Token signature is invalid</li>
 *   <li>Token is expired</li>
 *   <li>Token format is incorrect</li>
 * </ul>
 * </p>
 */
public class InvalidTokenException extends DomainException {
    
    public InvalidTokenException(String message) {
        super(message);
    }
    
    public InvalidTokenException(String message, Throwable cause) {
        super(message, cause);
    }
    
    @Override
    public String getErrorCode() {
        return "INVALID_TOKEN";
    }
}

