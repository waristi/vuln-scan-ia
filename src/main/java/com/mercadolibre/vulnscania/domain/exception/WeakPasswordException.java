package com.mercadolibre.vulnscania.domain.exception;

/**
 * Domain exception thrown when a password does not meet strength requirements.
 *
 * <p>This exception is thrown during user registration or password updates
 * when the provided password does not satisfy the security policy.</p>
 */
public class WeakPasswordException extends DomainException {

    public WeakPasswordException(String message) {
        super(message);
    }

    public WeakPasswordException(String message, Throwable cause) {
        super(message, cause);
    }

    @Override
    public String getErrorCode() {
        return "WEAK_PASSWORD";
    }
}

