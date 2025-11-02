package com.mercadolibre.vulnscania.domain.exception;

public class InvalidScoreException extends DomainException {
    
    private final double providedScore;
    
    public InvalidScoreException(double providedScore) {
        super("Invalid severity score: " + providedScore + ". Must be between 0.0 and 10.0");
        this.providedScore = providedScore;
    }
    
    @Override
    public String getErrorCode() {
        return "INVALID_SCORE";
    }
    
    public double getProvidedScore() {
        return providedScore;
    }
}

