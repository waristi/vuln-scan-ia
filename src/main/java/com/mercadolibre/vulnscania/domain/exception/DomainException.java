package com.mercadolibre.vulnscania.domain.exception;

/**
 * Abstract base class for all domain exceptions.
 * 
 * <p>Represents business rule violations, not technical errors. All domain exceptions
 * extend this class to provide consistent error handling and error code identification.</p>
 * 
 * <p><strong>Purpose</strong>:</p>
 * <ul>
 *   <li>Separate business logic errors from technical errors</li>
 *   <li>Provide structured error codes for API responses</li>
 *   <li>Enable consistent exception handling in infrastructure layer</li>
 * </ul>
 */
public abstract class DomainException extends RuntimeException {
    
    protected DomainException(String message) {
        super(message);
    }
    
    protected DomainException(String message, Throwable cause) {
        super(message, cause);
    }
    
    /**
     * Returns a unique error code identifying the exception type.
     * 
     * <p>Error codes are useful for:
     * <ul>
     *   <li>Internationalization</li>
     *   <li>Structured logging</li>
     *   <li>API error responses</li>
     *   <li>Client-side error handling</li>
     * </ul>
     * </p>
     * 
     * @return Unique error code (e.g., "INVALID_CREDENTIALS", "USER_NOT_FOUND")
     */
    public abstract String getErrorCode();
}
