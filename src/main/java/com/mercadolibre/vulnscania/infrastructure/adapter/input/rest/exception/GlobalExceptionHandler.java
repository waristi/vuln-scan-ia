package com.mercadolibre.vulnscania.infrastructure.adapter.input.rest.exception;

import com.mercadolibre.vulnscania.domain.exception.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.time.Instant;
import java.util.HashMap;
import java.util.Map;

/**
 * Global exception handler for REST controllers.
 * Translates domain exceptions to appropriate HTTP responses.
 * 
 * Clean architecture: Maps domain exceptions to HTTP concerns in infrastructure layer.
 */
@RestControllerAdvice
public class GlobalExceptionHandler {
    
    private static final Logger log = LoggerFactory.getLogger(GlobalExceptionHandler.class);
    
    /**
     * Handles domain-specific exceptions.
     */
    @ExceptionHandler(VulnerabilityNotFoundException.class)
    public ResponseEntity<ErrorResponse> handleVulnerabilityNotFound(VulnerabilityNotFoundException ex) {
        log.warn("Vulnerability not found: {}", ex.getVulnerabilityId());
        return buildErrorResponse(HttpStatus.NOT_FOUND, ex);
    }
    
    @ExceptionHandler(ApplicationNotFoundException.class)
    public ResponseEntity<ErrorResponse> handleApplicationNotFound(ApplicationNotFoundException ex) {
        log.warn("Application not found: {}", ex.getApplicationId());
        return buildErrorResponse(HttpStatus.NOT_FOUND, ex);
    }
    
    @ExceptionHandler(ApplicationAlreadyExistsException.class)
    public ResponseEntity<ErrorResponse> handleApplicationAlreadyExists(ApplicationAlreadyExistsException ex) {
        log.warn("Application already exists: {}", ex.getApplicationName());
        return buildErrorResponse(HttpStatus.CONFLICT, ex);
    }
    
    @ExceptionHandler(InvalidCredentialsException.class)
    public ResponseEntity<ErrorResponse> handleInvalidCredentials(InvalidCredentialsException ex) {
        // Don't log specific credential details to avoid information disclosure
        log.warn("Invalid credentials provided");
        return buildErrorResponse(HttpStatus.UNAUTHORIZED, ex);
    }
    
    @ExceptionHandler(InvalidTokenException.class)
    public ResponseEntity<ErrorResponse> handleInvalidToken(InvalidTokenException ex) {
        log.warn("Invalid token: {}", ex.getMessage());
        return buildErrorResponse(HttpStatus.UNAUTHORIZED, ex);
    }
    
    @ExceptionHandler(UserAlreadyExistsException.class)
    public ResponseEntity<ErrorResponse> handleUserAlreadyExists(UserAlreadyExistsException ex) {
        // Don't log the full message to avoid information disclosure
        log.warn("Registration attempt with existing username");
        return buildErrorResponse(HttpStatus.CONFLICT, ex);
    }

    @ExceptionHandler(com.mercadolibre.vulnscania.domain.exception.WeakPasswordException.class)
    public ResponseEntity<ErrorResponse> handleWeakPassword(com.mercadolibre.vulnscania.domain.exception.WeakPasswordException ex) {
        // Don't log password details
        log.warn("Weak password provided during registration");
        return buildErrorResponse(HttpStatus.BAD_REQUEST, ex);
    }
    
    @ExceptionHandler(InvalidCveIdException.class)
    public ResponseEntity<ErrorResponse> handleInvalidCveId(InvalidCveIdException ex) {
        log.warn("Invalid CVE ID: {}", ex.getProvidedValue());
        return buildErrorResponse(HttpStatus.BAD_REQUEST, ex);
    }
    
    @ExceptionHandler(InvalidScoreException.class)
    public ResponseEntity<ErrorResponse> handleInvalidScore(InvalidScoreException ex) {
        log.warn("Invalid score: {}", ex.getProvidedScore());
        return buildErrorResponse(HttpStatus.BAD_REQUEST, ex);
    }
    
    @ExceptionHandler(InvalidStateTransitionException.class)
    public ResponseEntity<ErrorResponse> handleInvalidStateTransition(InvalidStateTransitionException ex) {
        log.warn("Invalid state transition from {} to {}", ex.getCurrentState(), ex.getAttemptedState());
        return buildErrorResponse(HttpStatus.BAD_REQUEST, ex);
    }
    
    @ExceptionHandler(AssessmentAlreadyCompletedException.class)
    public ResponseEntity<ErrorResponse> handleAssessmentAlreadyCompleted(AssessmentAlreadyCompletedException ex) {
        log.warn("Assessment already completed: {}", ex.getAssessmentId());
        return buildErrorResponse(HttpStatus.CONFLICT, ex);
    }
    
    /**
     * Handles validation errors from @Valid annotations.
     */
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ErrorResponse> handleValidationErrors(MethodArgumentNotValidException ex) {
        Map<String, String> errors = new HashMap<>();
        ex.getBindingResult().getAllErrors().forEach(error -> {
            String fieldName = ((FieldError) error).getField();
            String errorMessage = error.getDefaultMessage();
            errors.put(fieldName, errorMessage);
        });
        
        log.warn("Validation errors: {}", errors);
        
        ErrorResponse errorResponse = new ErrorResponse(
            "VALIDATION_ERROR",
            "Validation failed",
            errors,
            Instant.now()
        );
        
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errorResponse);
    }
    
    /**
     * Handles generic domain exceptions.
     */
    @ExceptionHandler(DomainException.class)
    public ResponseEntity<ErrorResponse> handleDomainException(DomainException ex) {
        log.error("Domain exception: {}", ex.getMessage());
        return buildErrorResponse(HttpStatus.BAD_REQUEST, ex);
    }
    
    /**
     * Handles unexpected errors.
     *
     * <p>Security: Does not expose internal error details to clients in production.
     * Only logs full details server-side for debugging.</p>
     */
    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorResponse> handleUnexpectedException(Exception ex) {
        // Log full error details server-side for debugging
        log.error("Unexpected error: {}", ex.getClass().getSimpleName(), ex);
        
        // Don't expose internal error details to clients
        ErrorResponse errorResponse = new ErrorResponse(
            "INTERNAL_ERROR",
            "An unexpected error occurred. Please contact support if the problem persists.",
            Map.of(), // Don't include error message details
            Instant.now()
        );
        
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(errorResponse);
    }
    
    /**
     * Builds error response from domain exception.
     */
    private ResponseEntity<ErrorResponse> buildErrorResponse(HttpStatus status, DomainException ex) {
        ErrorResponse errorResponse = new ErrorResponse(
            ex.getErrorCode(),
            ex.getMessage(),
            Map.of(),
            Instant.now()
        );
        
        return ResponseEntity.status(status).body(errorResponse);
    }
    
    /**
     * Error response DTO.
     */
    public record ErrorResponse(
        String errorCode,
        String message,
        Map<String, String> details,
        Instant timestamp
    ) {}
}

