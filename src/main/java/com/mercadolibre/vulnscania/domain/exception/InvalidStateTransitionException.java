package com.mercadolibre.vulnscania.domain.exception;

public class InvalidStateTransitionException extends DomainException {
    
    private final String currentState;
    private final String attemptedState;
    
    public InvalidStateTransitionException(String currentState, String attemptedState) {
        super("Cannot transition from state '" + currentState + "' to '" + attemptedState + "'");
        this.currentState = currentState;
        this.attemptedState = attemptedState;
    }
    
    @Override
    public String getErrorCode() {
        return "INVALID_STATE_TRANSITION";
    }
    
    public String getCurrentState() {
        return currentState;
    }
    
    public String getAttemptedState() {
        return attemptedState;
    }
}

