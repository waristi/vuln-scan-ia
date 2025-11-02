package com.mercadolibre.vulnscania.domain.exception;

public class ApplicationAlreadyExistsException extends DomainException {
    
    private final String applicationName;
    
    public ApplicationAlreadyExistsException(String applicationName) {
        super("Application already exists with name: " + applicationName);
        this.applicationName = applicationName;
    }
    
    @Override
    public String getErrorCode() {
        return "APPLICATION_ALREADY_EXISTS";
    }
    
    public String getApplicationName() {
        return applicationName;
    }
}

