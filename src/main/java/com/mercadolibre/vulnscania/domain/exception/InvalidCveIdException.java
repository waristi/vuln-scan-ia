package com.mercadolibre.vulnscania.domain.exception;

public class InvalidCveIdException extends DomainException {
    
    private final String providedValue;
    
    public InvalidCveIdException(String providedValue) {
        super("Invalid CVE ID format: " + providedValue + ". Expected format: CVE-YYYY-NNNNN");
        this.providedValue = providedValue;
    }
    
    @Override
    public String getErrorCode() {
        return "INVALID_CVE_ID";
    }
    
    public String getProvidedValue() {
        return providedValue;
    }
}

