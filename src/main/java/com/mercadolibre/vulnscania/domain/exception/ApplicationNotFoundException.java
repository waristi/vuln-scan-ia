package com.mercadolibre.vulnscania.domain.exception;

public class ApplicationNotFoundException extends DomainException {
    
    private final String applicationId;
    
    public ApplicationNotFoundException(String applicationId) {
        super("Application not found with ID: " + applicationId);
        this.applicationId = applicationId;
    }
    
    @Override
    public String getErrorCode() {
        return "APPLICATION_NOT_FOUND";
    }
    
    public String getApplicationId() {
        return applicationId;
    }
}

