package com.mercadolibre.vulnscania.domain.exception;

/**
 * Clase base abstracta para todas las excepciones de dominio.
 * Representa errores de reglas de negocio, no errores técnicos.
 */
public abstract class DomainException extends RuntimeException {
    
    protected DomainException(String message) {
        super(message);
    }
    
    protected DomainException(String message, Throwable cause) {
        super(message, cause);
    }
    
    /**
     * Código único de error para identificar el tipo de excepción.
     * Útil para internacionalización y logging estructurado.
     */
    public abstract String getErrorCode();
}
